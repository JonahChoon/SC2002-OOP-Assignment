package sc2002;

import java.util.Scanner;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Patient extends User {
    private String patientID;
    private String dateOfBirth;
    private String gender;
    private String bloodType;
    private String emailAddress;
    private String phoneNumber;

    public Patient(String patientID, String name, String dateOfBirth, String gender, String bloodType,
            String emailAddress, String phoneNumber) {
        super(patientID, name, "Patient");
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodType = bloodType;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    public void displayMenu(Scanner scanner) {
        boolean sessionActive = true;
        while (sessionActive) {
            MenuHandler.displayPatientMenu();
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    MedicalRecordHandler.viewMedicalRecord(getPatientID());
                    break;
                case 2:
                    PersonalInfoHandler.updatePersonalInformation(this, scanner);
                    break;
                case 3:
                	AppointmentSlotHandler.viewAvailableAppointmentSlots(scanner);
                	break;
                case 4:
                    AppointmentSlotHandler.scheduleAppointment(scanner, this); // Call the scheduleAppointment method
                    break;
                case 5:
                   AppointmentSlotHandler.rescheduleAppointment(scanner, this); // 'this' refers to the current patient object
                    break;
                case 6:
                	AppointmentSlotHandler.cancelAppointment(scanner, this);
                	break;
                case 7:
                	AppointmentSlotHandler.viewAppointments(scanner, this);
                	break;
                case 8:
                	AppointmentSlotHandler.viewAllAppointmentsAndOutcomes(scanner, this);
                	break;
                case 9:
                    System.out.println("Logging out...\n");
                    LoginHandler.logout(this.getUserID(), "Patient");
                    sessionActive = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserName() {
        return super.name;
    }

    public static Map<String, Patient> loadPatientsFromCSV(String csvFile) {
        Map<String, Patient> patients = new HashMap<>();
        String line = "";
        String splitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                if (data.length == 7) {
                    String patientID = data[0].trim();
                    String patientName = data[1].trim();
                    String dob = data[2].trim();
                    String gender = data[3].trim();
                    String bloodType = data[4].trim();
                    String email = data[5].trim();
                    String phone = data[6].trim();

                    Patient patient = new Patient(patientID, patientName, dob, gender, bloodType, email, phone);
                    patients.put(patientID, patient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patients;
    }


    @Override
    public void accessPortal() {
        System.out.println("Patient Portal accessed by " + name);
    }
}



