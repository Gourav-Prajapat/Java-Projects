import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem{

    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db" ;
    private static final String username = "admin" ;
    private static final String password = "admin@1234" ;

    public static void main(String[] args) throws ClassNotFoundException, SQLException{

        try {
           Class.forName("com.mysql.cj.jdbc.Driver");
       }catch(ClassNotFoundException e){
           System.out.println(e.getMessage());
       }

       try {
           Connection connection = DriverManager.getConnection(url, username, password);
           Scanner scanner = new Scanner(System.in);
           while(true) {
               System.out.println();
               System.out.println("HOTEL MANAGEMENT SYSTEM");
               System.out.println("1. Reserve a room");
               System.out.println("2. View Reservation");
               System.out.println("3. Get room number");
               System.out.println("4. Update Reservation");
               System.out.println("5. Delete Reservation");
               System.out.println("0. Exit");
               System.out.println("Please, Choose an option");
               int choice = scanner.nextInt();

               switch (choice) {
                   case 1 :
                       reserveRoom (connection, scanner);
                       break;
                   case 2 :
                       viewReservation (connection);
                       break;
                   case 3 :
                       getRoomNumber (connection, scanner);
                       break;
                   case 4 :
                       updateReservation (connection, scanner);
                       break;
                   case 5 :
                       deleteReservation (connection, scanner);
                       break;
                   case 0 :
                       Exit ();
                       scanner.close();
                       break;
                   default:
                       System.out.println("Invalid choice. Please try again.");
               }

           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }catch (InterruptedException e){
           throw new RuntimeException();
       }

    }


    private static void reserveRoom (Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful");
                } else {
                    System.out.println("Reservation failed");
                }
            }
        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    private static void viewReservation (Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date From reservations";

        try(Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Current Reservation: ");
            System.out.println("+----------------+------------------+-----------------+-------------------+------------------+");
            System.out.println("| Reservation ID | Guest            | Room number     | Contact number    | Reservation Date |");
            System.out.println("+----------------+------------------+-----------------+-------------------+------------------+");

            while(resultSet.next()){
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getString("reservation_date");

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+------------------+-----------------+-------------------+------------------+");
        }
    }

    private static void getRoomNumber (Connection connection, Scanner scanner){
        try {
            System.out.println("Enter the reservation ID: ");
            int reservationID = scanner.nextInt();
            System.out.println("Enter the Guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations" +
                     "WHERE reservation_id = " + reservationID +
                     "AND guest_name = '" + guestName +"'";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)){
                if (resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationID +
                            "and Guest " + guestName + "is : " + roomNumber);
                }else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation (Connection connection, Scanner scanner){
        try {
            System.out.println("Enter the reservation ID to update: ");
            int reservationID = scanner.nextInt();

            if (!reservationExists(connection, reservationID)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter guest name: ");
            String newGuestName = scanner.next();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = " + newGuestName + "," +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = " + newContactNumber + " WHERE reservation_id = " + reservationID;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0){
                    System.out.println("Reservation updated successfully!");
                }else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation    (Connection connection, Scanner scanner){
        try {
            System.out.println("Enter the reservation ID to delete: ");
            int reservationID = scanner.nextInt();

            if (!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given ID. ");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationID;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists (Connection connection, int reservationID){
        try {
        String sql = "SELECT reservation_id from reservations where reservation_id = " + reservationID;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql))
            {
                return resultSet.next();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void Exit () throws InterruptedException {
        System.out.println("Existing the system");
       int i = 5;
       while (i!=0){
           System.out.println(".");
           Thread.sleep(1000);
           i-- ;
       }
        System.out.println();
        System.out.println("Thank You For Using The Hotel Reservation System");
    }
}