import java.util.ArrayList;

public class Account {
    private String name; //The name of the account
    private String uuid; //The account ID number
    private User holder; //The User object that owns this account
    private ArrayList<Transaction> transactions; //The list of transactions for this account

    //Create a new Account
    //parameter name - the name of the account
    //parameter holder - the User object that holds this account
    //parameter theBank - the bank that issues the account

    public Account(String name, User holder, Bank theBank){

        //set the account name and holder
        this.name = name;
        this.holder = holder;

        //get new account UUID
        this.uuid = theBank.getNewAccountUUID();

        //init transactions
        this.transactions = new ArrayList<Transaction>();
    }

    //Get the account ID
    public String getUUID(){
        return this.uuid;
    }

    //Get summary line for the account
    //returns - the string summary
    public String getSummaryLine(){

        //get the account's balance
        double balance = this.getBalance();

        //format the summary line, depending on the whether the balance is negative
        if(balance>=0){
            return String.format("%s : $%.02f : %s", this.uuid, balance,
                    this.name);
        } else {
            return String.format("%s : $(%.02f) : %s", this.uuid, balance,
                    this.name);

        }
    }

    //Get the balance of this account by adding the amounts of the transactions
    public double getBalance(){
        double balance = 0;
        for (Transaction t : this.transactions){
            balance += t.getAmount();
        }
        return balance;
    }

    //Print the transaction history of the account
    public void printTransactionHistory(){
        System.out.printf("\nTransaction history for account %s\n", this.uuid);
        for (int t = this.transactions.size()-1; t >= 0; t--){
            System.out.println(this.transactions.get(t).getSummaryLine());
        }
        System.out.println();
    }

    //Add a new transaction in this account
    public void addTransaction(double amount, String memo){
        //create new transaction object and add it to or list
        Transaction newTransaction = new Transaction(amount, memo, this);
        this.transactions.add(newTransaction);
    }
}
