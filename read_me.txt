https://github.com/aruzhan1106

This project consists of five different classes. When the system starts the user is prompted with user id and 
user pin. On entering the details successfully, then ATM functionalities are unlocked. The project allows to 
perform following operations:

1) Transactions History
2) Withdraw
3) Deposit
4) Transfer
5) Quit

User class: has 5 instances, getters for these instances, a constructor, and various methods like validatePin,
printAccountsSummary, printAccountTransactionHistory

Account class: has 4 instances, a constuctor, and methods like getSummaryLine, getBalance

Transaction class: has 4 instances, two constuctors, and a method getSummaryLine

Bank class: has 3 instances, a constuctor, and methods like getNewUserUUID, getNewAccountUUID, addUser

ATM class: has methods like mainMenuPrompt, printUserMenu, showTransactionHistory