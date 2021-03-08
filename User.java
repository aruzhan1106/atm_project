import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;

public class User {
    private String firstName; //The first name of the user
    private String lastName; //The last name of the user
    private String uuid; //The ID number of the user
    private byte pinHash[]; //The MD5 hash of the user's PIN
    private ArrayList<Account> accounts; //The list of accounts for this user

    //Create a new user
    //parameter firstName - the user's first name
    //parameter lastName - the user's last name
    //parameter pin - the user's account pin
    //parameter theBank - the Bank object that the user is a customer of
    public User(String firstName, String lastName, String pin, Bank theBank){
        //set user's name
        this.firstName = firstName;
        this.lastName = lastName;

        //store the pin's MD5 hash, rather than the original value
        //for security reasons
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch(NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        //get a new, unique universal ID for the user
        this.uuid = theBank.getNewUserUUID();

        //create empty list of accounts
        this.accounts = new ArrayList<Account>();

        //print log message
        System.out.printf("New user %s, %s with ID %s created.\n", lastName,
                firstName, this.uuid);
    }

    //Add an account for the user
    //parameter anAcct - the account to add
    public void addAccount(Account anAcct){
        this.accounts.add(anAcct);
    }
    //Returns the user's UUID
    public String getUUID(){
        return this.uuid;
    }

    //Check whether a given pin matches the true User pin
    //parameter aPin - the pin to check
    //returns - whether the pin is valid or not
    public boolean validatePin(String aPin){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(aPin.getBytes()), this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    //Get the user's first name
    public String getFirstName(){
        return this.firstName;
    }

    //Get the user's last name
    public String getLastName(){
        return this.lastName;
    }

    //Print summaries for the accounts of this user
    public void printAccountsSummary(){
        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        for (int a = 0; a < this.accounts.size(); a++){
            System.out.printf("  %d) %s\n", a+1, this.accounts.get(a).getSummaryLine());
        }
        System.out.println();
    }

    //Get the number of accounts of the user
    public int numAccounts(){
        return this.accounts.size();
    }

    //Print transaction history for a particular account
    //parameter acctIdx - the index of the account to use

    public void printAccountTransactionHistory(int acctIdx){
        this.accounts.get(acctIdx).printTransactionHistory();
    }

    //Get the balance of a particular account
    //parameter acctIdx - the index of the account
    //returns - the balance of the account
    public double getAcctBalance(int acctIdx){
        return this.accounts.get(acctIdx).getBalance();
    }

    //Get the UUID of a particular account
    //parameter acctIdx - the index of the account to use
    //returns - the UUID of the account
    public String getAcctUUID(int acctIdx){
        return this.accounts.get(acctIdx).getUUID();
    }

    public void addAcctTransaction(int acctIdx, double amount, String memo){
        this.accounts.get(acctIdx).addTransaction(amount, memo);
    }
}
