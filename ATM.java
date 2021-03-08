import javax.annotation.processing.SupportedSourceVersion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Currency;
import java.util.Scanner;

public class ATM {

    public static void main(String[] args){

        //initialize Scanner
        Scanner sc = new Scanner(System.in);

        //initialize Bank
        Bank theBank = new Bank("Halyk Bank");

        //add a user, which also creates a savings account
        User aUser = theBank.addUser("Jane", "Doe", "1234");

        //add a checking account for our user
        Account newAccount = new Account("Checking", aUser, theBank);
        aUser.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while (true) {
            //stay in the login prompt until successful login
            curUser = ATM.mainMenuPrompt(theBank, sc);

            //stay in main menu until user quits
            ATM.printUserMenu(curUser, sc);
        }
    }

    //Print the ATM's login menu
    //parameter theBank - the Bank object whose accounts to use
    //parameter sc - the Scanner object to use for user input
    //returns - the authenticated User object
    public static User mainMenuPrompt(Bank theBank, Scanner sc){
        String userID;
        String pin;
        User authUser;

        //prompt the user for user ID/pin combo until a correct one is reached
        do {
            System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
            System.out.print("Enter user ID: ");
            userID = sc.nextLine();
            System.out.print("Enter pin: ");
            pin = sc.nextLine();

            //try to get the user object corresponding to the ID and pin combo
            authUser = theBank.userLogin(userID, pin);
            if (authUser == null){
                System.out.println("Incorrect user ID/pin combination. " +
                        "Please try again.");
            }
        } while(authUser == null); //continue looping until successful login

        return authUser;
    }

    public static void printUserMenu(User theUser, Scanner sc){

        //print a summary of the user's accounts
        theUser.printAccountsSummary();

        //initialization
        int choice;
        Connection connection = null;
        Statement statement = null;

        ConnectDB obj_ConnectDB = new ConnectDB();
        connection = obj_ConnectDB.get_connection();

        try{
            String queryInsert = "INSERT INTO User_ VALUES ('" + theUser.getFirstName() + "','" +
                    theUser.getLastName() + "','" + theUser.getUUID() + "')";
            statement = connection.createStatement();
            statement.executeUpdate(queryInsert);

            //System.out.println("Done");
            statement.close();
            connection.close();

        }
        catch(Exception e){
            System.out.println(e);
        }

        //user menu
        do {
            System.out.printf("Welcome %s, what would you like to do?\n",
                    theUser.getFirstName());
            System.out.print(" 1) Show account transaction history\n");
            System.out.print(" 2) Withdraw\n");
            System.out.print(" 3) Deposit\n");
            System.out.print(" 4) Transfer\n");
            System.out.print(" 5) Quit\n");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please choose 1-5");
            } } while (choice <1 || choice >5);

            //process the choice
            switch(choice) {
                case 1:
                    ATM.showTransactionHistory(theUser, sc);
                    break;
                case 2:
                    ATM.withdrawFunds(theUser, sc);
                    break;
                case 3:
                    ATM.depositFunds(theUser, sc);
                    break;
                case 4:
                    ATM.transferFunds(theUser, sc);
                    break;
                case 5:
                    //gobble up the rest of previous input
                    sc.nextLine();
                    break;
            }

            //redisplay this menu unless the user wants to quit
            if(choice!=5) {
                ATM.printUserMenu(theUser, sc);
            }
        }

        //Show the transaction history for an account
        //parameter theUser - the logged-in User object
        //parameter sc - the Scanner object used for user input
        public static void showTransactionHistory(User theUser, Scanner sc){
            int theAcct;

            //get account whose transaction history to look at
            do {
                System.out.printf("Enter the number (1-%d) of the account\n" +
                        "whose transactions you want to see: ",
                        theUser.numAccounts());
                theAcct = sc.nextInt()-1;
                if(theAcct <0 || theAcct>=theUser.numAccounts()){
                    System.out.println("Invalid account. Please try again.");
                }

            } while(theAcct <0 || theAcct>=theUser.numAccounts());

            //print the transaction history
            theUser.printAccountTransactionHistory(theAcct);
        }

        //Process transferring funds from one account to another
        //parameter theUser - the logged-in User object
        //parameter sc - the Scanner object used for user input
        public static void transferFunds(User theUser, Scanner sc) {
            int fromAcct;
            int toAcct;
            double amount;
            double acctBal;

            //get the account to transfer from
            do {
                System.out.printf("Enter the number (1-%d) of the account\n" +
                        "to transfer from: ", theUser.numAccounts());
                fromAcct = sc.nextInt()-1;
                if (fromAcct < 0 || fromAcct >= theUser.numAccounts()){
                    System.out.println("Invalid account. Please try again.");
                } }
            while(fromAcct <0 || fromAcct>=theUser.numAccounts());
            acctBal = theUser.getAcctBalance(fromAcct);

            //get the account to transfer to
            do {
                System.out.printf("Enter the number (1-%d) of the account\n" +
                        "to transfer to: ", theUser.numAccounts());
                toAcct = sc.nextInt()-1;
                if (toAcct < 0 || toAcct >= theUser.numAccounts()){
                    System.out.println("Invalid account. Please try again.");
                } }
            while(toAcct <0 || toAcct>=theUser.numAccounts());

            //get the amount to transfer
            do{
                System.out.printf("Enter the amount to transfer (max $%.02f): $",
                        acctBal);
                amount = sc.nextDouble();
                if (amount < 0){
                    System.out.println("Amount must be greater than zero.");

                } else if (amount > acctBal){
                    System.out.printf("Amount must not be greater than\n"+
                            "balance of $%.02f.\n", acctBal);
                }
            }
            while(amount < 0 || amount > acctBal);

            //finally do the transfer
            theUser.addAcctTransaction(fromAcct, -1*amount, String.format(
                    "Transfer to account %s", theUser.getAcctUUID(toAcct)));
            theUser.addAcctTransaction(toAcct, amount, String.format(
                    "Transfer to account %s", theUser.getAcctUUID(fromAcct)));
        }

        //Process a fund withdraw from an account
        //parameter theUser - the logged-in User object
        //parameter sc - the Scanner object used for user input
        public static void withdrawFunds(User theUser, Scanner sc) {
            int fromAcct;
            double amount;
            double acctBal;
            String memo;

            //get the account to withdraw from
            do {
                System.out.printf("Enter the number (1-%d) of the account\n" +
                        "to withdraw from: ", theUser.numAccounts());
                fromAcct = sc.nextInt()-1;
                if (fromAcct < 0 || fromAcct >= theUser.numAccounts()){
                    System.out.println("Invalid account. Please try again.");
                } }
            while(fromAcct <0 || fromAcct>=theUser.numAccounts());
            acctBal = theUser.getAcctBalance(fromAcct);

            //get the amount to withdraw
            do{
                System.out.printf("Enter the amount to withdraw (max $%.02f): $",
                        acctBal);
                amount = sc.nextDouble();
                if (amount < 0){
                    System.out.println("Amount must be greater than zero.");

                } else if (amount > acctBal){
                    System.out.printf("Amount must not be greater than\n"+
                            "balance of $%.02f.\n", acctBal);
                }
            }
            while(amount < 0 || amount > acctBal);

            //gobble up the rest of previous input
            sc.nextLine();

            //get a memo
            System.out.print("Enter a memo: ");
            memo = sc.nextLine();

            //do the withdraw
            theUser.addAcctTransaction(fromAcct,-1*amount, memo);

        }

        //Process a fund deposit to an account
        //parameter theUser - the logged-in User object
        //parameter sc - the Scanner object used for user input
        public static void depositFunds(User theUser, Scanner sc){
            int toAcct;
            double amount;
            double acctBal;
            String memo;

            //get the account to deposit into
            do {
                System.out.printf("Enter the number (1-%d) of the account\n" +
                        "to deposit into: ", theUser.numAccounts());
                toAcct = sc.nextInt()-1;
                if (toAcct < 0 || toAcct >= theUser.numAccounts()){
                    System.out.println("Invalid account. Please try again.");
                } }
            while(toAcct <0 || toAcct>=theUser.numAccounts());
            acctBal = theUser.getAcctBalance(toAcct);

            //get the amount to deposit
            do{
                System.out.print("Enter the amount to deposit: $");
                amount = sc.nextDouble();
                if (amount < 0){
                    System.out.println("Amount must be greater than zero.");
                }
            }
            while(amount < 0);

            //gobble up the rest of previous input
            sc.nextLine();

            //get a memo
            System.out.print("Enter a memo: ");
            memo = sc.nextLine();

            //do the deposit
            theUser.addAcctTransaction(toAcct, amount, memo);
        }

    }
