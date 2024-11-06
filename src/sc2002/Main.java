package sc2002;

import java.util.Scanner;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LoginHandler.loadLoginData();

        while (true) {

            System.out.println("=========================================");
            System.out.println("       Hospital Management System");
            System.out.println("=========================================\n");

            System.out.print("Enter UserID: ");
            String userID = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            String role = LoginHandler.authenticate(userID, password);

            if (role != null) {
                if (LoginHandler.isFirstLogin(userID)) {
                    System.out.println("This is your first login. You are required to change your password.");

                    // Prompt for new password with validation
                    String newPassword;
                    while (true) {
                        System.out.print("Enter new Password (must contain at least 8 characters, including uppercase, lowercase, digit, and special character): ");
                        newPassword = scanner.nextLine().trim();

                        // Check if the password is valid
                        if (LoginHandler.isValidPassword(newPassword)) {
                            break; // Password meets requirements
                        }
                        System.out.println("Password does not meet complexity requirements.");
                    }

                    LoginHandler.changePassword(userID, newPassword);  // Change password
                    System.out.println("\nPassword successfully changed.");
                    System.out.println("Press ENTER to continue...");
                    scanner.nextLine();
                }

                String username = null;

                switch (role) {
                case "Administrator":
                    Map<String, Administrator> admins = Administrator.loadAdministratorsFromCSV("data/Staff_Records.csv");
                    Administrator admin = admins.get(userID);
                    if (admin != null) {
                        username = admin.getUserName();
                        System.out.println("\nWelcome, " + username + "!");
                        admin.displayMenu(scanner);
                    } else {
                        System.out.println("Administrator not found.");
                    }
                    break;

                case "Doctor":
                    Map<String, Doctor> doctors = Doctor.loadDoctorsFromCSV("data/Staff_Records.csv");
                    Doctor doctor = doctors.get(userID);

                    if (doctor != null) {
                        username = doctor.getUserName();
                        System.out.println("\nWelcome, Dr. " + username + "!");
                        doctor.displayMenu(scanner);
                    } else {
                        System.out.println("Doctor not found.");
                    }
                    break;

                case "Patient":
                    Map<String, Patient> patients = Patient.loadPatientsFromCSV("data/Patient_Records.csv");
                    Patient patient = patients.get(userID);
                    if (patient != null) {
                        username = patient.getUserName();
                        System.out.println("\nWelcome, " + username + "!");
                        patient.displayMenu(scanner);
                    } else {
                        System.out.println("Patient not found.");
                    }
                    break;

                case "Pharmacist":
                    Map<String, Pharmacist> pharmacists = Pharmacist.loadPharmacistsFromCSV("data/Staff_Records.csv");
                    Pharmacist pharmacist = pharmacists.get(userID);
                    if (pharmacist != null) {
                        username = pharmacist.getUserName();
                        System.out.println("\nWelcome, " + username + "!");
                        pharmacist.displayMenu(scanner);
                    } else {
                        System.out.println("Pharmacist not found.");
                    }
                    break;

                default:
                    System.out.println("Invalid role. Please check your login details.");
                }
            } else {
                //System.out.println("Login failed. Invalid UserID or Password.");
            }
        }
    }
}
