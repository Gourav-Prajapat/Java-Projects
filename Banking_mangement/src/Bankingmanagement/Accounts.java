package Bankingmanagement;

import java.sql.*;
import java.util.Scanner;

public class Accounts{
    private final Connection connection;
    private final Scanner scanner;

    public Accounts(Connection connection , Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long open_account(String email){
        if(!account_exist(email)){
            String open_account_query = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VAlUES (?, ?, ?, ?, ?)";
            scanner.nextLine();
            System.out.print("Enter the Full name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter the Initial amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter the Security Pin: ");
            String security_pin = scanner.nextLine();

            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int affectedRows = preparedStatement.executeUpdate();

                if(affectedRows > 0){
                    return account_number;
                }else {
                    throw new RuntimeException("Account creation failed!");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account already exist");
    }

    public long getAccount_number(String email){
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("account number doesn't exist");
    }

    public long generateAccountNumber(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number FROM Accounts ORDER BY account_number DESC LIMIT 1");
            if(resultSet.next()){
                long last_account_number = resultSet.getLong("account_number");
                return last_account_number+1;
            }else {
                return (long) (Math.random()*999999999);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public boolean account_exist(String email){
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
