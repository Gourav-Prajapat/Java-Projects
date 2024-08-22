package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital_management_system";
    private static final String userName = "admin";
    private static final String password = "admin@1234";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        try{
            Connection connection = DriverManager.getConnection(url, userName, password);
            Scanner scanner = new Scanner(System.in);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while(true){
                System.out.println("WELCOME TO HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add patient");
                System.out.println("2. View patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                System.out.println();

                switch (choice){
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(connection, scanner, patient, doctor);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM");
                        System.out.println();
                        return ;
                    default:
                        System.out.println("Enter Valid Choice!!!");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Connection connection, Scanner scanner, Patient patient, Doctor doctor){
        System.out.print("Enter Patient ID: ");
        int patientID = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorID = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if(patient.getPatientByID(patientID) && doctor.getDoctorByID(doctorID)){
            if(checkDoctorAvailability(doctorID, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setString(3, appointmentDate);
                    int affectedRows = preparedStatement.executeUpdate();
                    if(affectedRows > 0){
                        System.out.println("APPOINTMENT IS BOOKED!!!");
                    }else {
                        System.out.println("FAILED TO BOOK AN APPOINTMENT!!!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor is not available on this date !!!");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist !!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorID, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorID);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else {
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
