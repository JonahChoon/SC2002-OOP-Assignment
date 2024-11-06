package sc2002;

import java.util.Scanner;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Represents a patient in the hospital management system.
 */
public class Patient extends User {
	private String patientID;
	private String dateOfBirth;
	private String gender;
	private String bloodType;
	private String emailAddress;
	private String phoneNumber;

	/**
	 * Constructs a new Patient object.
	 *
	 * @param patientID    The unique ID of the patient.
	 * @param name         The name of the patient.
	 * @param dateOfBirth  The date of birth of the patient.
	 * @param gender       The gender of the patient.
	 * @param bloodType    The blood type of the patient.
	 * @param emailAddress The email address of the patient.
	 * @param phoneNumber  The phone number of the patient.
	 */
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

	/**
	 * Displays the patient menu and handles user input.
	 *
	 * @param scanner The scanner to read user input.
	 */
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
				AppointmentSlotHandler.rescheduleAppointment(scanner, this); // 'this' refers to the current patient
																				// object
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

	/**
	 * Gets the unique patient ID.
	 *
	 * @return The patient ID.
	 */
	public String getPatientID() {
		return patientID;
	}

	/**
	 * Gets the date of birth of the patient.
	 *
	 * @return The date of birth.
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Gets the gender of the patient.
	 *
	 * @return The gender.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Gets the blood type of the patient.
	 *
	 * @return The blood type.
	 */
	public String getBloodType() {
		return bloodType;
	}

	/**
	 * Gets the email address of the patient.
	 *
	 * @return The email address.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address of the patient.
	 *
	 * @param emailAddress The new email address.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the phone number of the patient.
	 *
	 * @return The phone number.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the phone number of the patient.
	 *
	 * @param phoneNumber The new phone number.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Sets the gender of the patient.
	 *
	 * @param gender The new gender of the patient.
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Gets the name of the patient.
	 *
	 * @return The patient's name.
	 */
	public String getUserName() {
		return super.name;
	}

	/**
	 * Loads patient data from a CSV file.
	 *
	 * @param csvFile The path to the CSV file.
	 * @return A map of patient IDs to Patient objects.
	 */
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

	/**
	 * Accesses the patient portal and logs the access.
	 */
	@Override
	public void accessPortal() {
		System.out.println("Patient Portal accessed by " + name);
	}
}
