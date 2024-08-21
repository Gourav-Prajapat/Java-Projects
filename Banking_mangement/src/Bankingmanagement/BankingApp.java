package Bankingmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/banking_system" ;
    private static final String userName = "admin" ;
    private static final String password = "admin@1234" ;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            Scanner scanner = new Scanner(System.in);
            User user = new User (connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);

            String email;
            long account_number;

            while(true){
                System.out.println("*** WELCOME TO BANKING SYSTEM ***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter Your Choice: ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        user.register();
                        System.out.flush();
                        break;
                    case 2:
                        email = user.login();
                        if(email != null){
                            System.out.println();
                            System.out.println("user Logged In!");
                            if(!accounts.account_exist(email)){
                                System.out.println("1. Open a New Bank account");
                                System.out.println("2. Exit");
                                System.out.print("Enter your choice: ");
                                if(scanner.nextInt() == 1){
                                    account_number = accounts.open_account(email);
                                    System.out.println("Account created successfully");
                                    System.out.println("Your account number: " +account_number);
                                }else {
                                    break;
                                }
                            }
                            account_number = accounts.getAccount_number(email);
                            int choice2 = 0;
                            while (choice2 !=5){
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. Log out");
                                System.out.print("\nEnter Your Choice: ");
                                choice2 = scanner.nextInt();
                                switch (choice2){
                                    case 1:
                                        accountManager.debit_money(account_number);
                                        break;
                                    case 2:
                                        accountManager.credit_money(account_number);
                                        break;
                                    case 3:
                                        accountManager.transfer_money(account_number);
                                        break;
                                    case 4:
                                        accountManager.getBalance(account_number);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.print("Enter Valid Choice!");
                                        break;
                                }
                            }
                        }else {
                            System.out.println("Incorrect Email or Password!");
                        }

                    case 3:
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM");
                        System.out.println("Exiting System");
                        return;

                    default:
                        System.out.println("Enter valid choice");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
