package sc2002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Doctor extends User {
	private String gender;
	private int age;
	private String emailAddress;
	private String contactNumber;

	public Doctor(String doctorID, String name, String gender, int age, String emailAddress, String contactNumber) {
		super(doctorID, name, "Doctor");
		this.gender = gender;
		this.age = age;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}

	public static Map<String, Doctor> loadDoctorsFromCSV(String csvFile) {
		Map<String, Doctor> doctors = new HashMap<>();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length == 8) {
					String doctorID = data[0].trim(); // Staff ID
					String doctorName = data[1].trim(); // Name
					String gender = data[3].trim(); // Gender
					int age = Integer.parseInt(data[4].trim()); // Age
					String emailAddress = data[5].trim(); // Email
					String contactNumber = data[6].trim(); // Contact number

					Doctor doctor = new Doctor(doctorID, doctorName, gender, age, emailAddress, contactNumber);
					doctors.put(doctorID, doctor);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doctors;
	}

	public static Map<String, String> loadDoctorNamesFromCSV(String csvFile) {
		Map<String, String> doctorNames = new HashMap<>();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			// Skip header
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);

				// Ensure valid number of columns and extract only ID and Name
				if (data.length >= 2) {
					String doctorID = data[0].trim(); // Staff ID from Staff_Records.csv
					String doctorName = data[1].trim(); // Doctor Name from Staff_Records.csv

					// Add the doctor ID and Name to the map
					doctorNames.put(doctorID, doctorName);

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doctorNames; // Return the map of doctor IDs and names
	}

	public String getUserName() {
		return super.name;
	}

	@Override
	public void accessPortal() {
		System.out.println("Doctor Portal accessed by " + name);
	}

	// Display the doctor menu and handle user input
	public void displayMenu(Scanner scanner) {
		boolean sessionActive = true;

		while (sessionActive) {
			MenuHandler.displayDoctorMenu();

			int choice = 0;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				continue;
			}

			switch (choice) {
			case 1:
				DoctorRecordHandler.viewAllPatientsUnderCare(this.getUserID());
				break;
			case 2:
				DoctorRecordHandler.updatePatientMedicalRecord(getUserID(), scanner);
				break;
			case 3:
				DoctorRecordHandler.viewDoctorAvailability(this.getUserID());
				break;
			case 4:
				DoctorRecordHandler.setDoctorAvailability(this.getUserID(), scanner);
				break;
			case 5:
				DoctorRecordHandler.approveOrDisapproveAppointments(this.getUserID(), scanner);
				break;
			case 6:
				AppointmentSlotHandler.viewUpcomingAppointments(this.getUserID());
				break;
			case 7:
				AppointmentSlotHandler.recordAppointmentOutcome(this.getUserID(), scanner);
				break;
			case 8:
				System.out.println("Logging out...\n");
				LoginHandler.logout(this.getUserID(), "Doctor");
				sessionActive = false;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	public static String loadDoctorName(String doctorID) {
	    Map<String, String> doctorNames = loadDoctorNamesFromCSV("data/Staff_Records.csv");
	    return doctorNames.getOrDefault(doctorID, "Unknown Doctor");
	}
	
	public String getGender() {
		return gender;
	}

	public int getAge() {
		return age;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setGender(String gender) {
	    this.gender = gender;
	}

	public void setAge(int age) {
	    this.age = age;
	}

	public void setEmailAddress(String emailAddress) {
	    this.emailAddress = emailAddress;
	}

	public void setContactNumber(String contactNumber) {
	    this.contactNumber = contactNumber;
	}
}




