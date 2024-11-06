package sc2002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles the updating of personal information for patients.
 */
public class PersonalInfoHandler {

	private static final String PATIENT_RECORDS_FILE = "data/Patient_Records.csv";

	/**
	 * Allows a patient to update their personal information.
	 *
	 * @param patient The patient whose information is being updated.
	 * @param scanner The scanner to read user input.
	 */
	public static void updatePersonalInformation(Patient patient, Scanner scanner) {
		boolean updateActive = true;

		while (updateActive) {
			MedicalRecordHandler.displayPatientDetails(patient); // Display current details
			System.out.println("=========================================");
			System.out.println("       Update Personal Information");
			System.out.println("=========================================");
			System.out.println("→ 1. Update Email Address");
			System.out.println("→ 2. Update Phone Number");
			System.out.println("→ 3. Update Password");
			System.out.println("→ 4. Back to Main Menu");
			System.out.println("=========================================");
			System.out.print("\nChoose an option: ");

			int choice = 0;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				continue;
			}

			switch (choice) {
			case 1:
				updateEmailAddress(patient, scanner);
				break;
			case 2:
				updatePhoneNumber(patient, scanner);
				break;
			case 3:
				updatePassword(patient, scanner);
				break;
			case 4:
				updateActive = false;
				System.out.println("");
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
	 * Updates the email address of the patient.
	 *
	 * @param patient The patient whose email is being updated.
	 * @param scanner The scanner to read user input.
	 */
	private static void updateEmailAddress(Patient patient, Scanner scanner) {
		System.out.print("Enter new email address: ");
		String email = scanner.nextLine();
		String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
		if (Pattern.matches(emailPattern, email)) {
			patient.setEmailAddress(email);
			System.out.println("Email address updated successfully.\n");
			saveUpdatedPatientInfo(patient);
		} else {
			System.out.println("Invalid email format. Try again.");
		}
	}

	/**
	 * Updates the phone number of the patient.
	 *
	 * @param patient The patient whose phone number is being updated.
	 * @param scanner The scanner to read user input.
	 */
	private static void updatePhoneNumber(Patient patient, Scanner scanner) {
		System.out.print("Enter new phone number: ");
		String phone = scanner.nextLine();
		if (phone.matches("[89]\\d{7}")) {
			patient.setPhoneNumber(phone);
			System.out.println("Phone number updated successfully.\n");
			saveUpdatedPatientInfo(patient);
		} else {
			System.out.println("Invalid phone number format. It should be 8 digits and start with 8 or 9.");
		}
	}

	/**
	 * Updates the password of the patient.
	 *
	 * @param patient The patient whose password is being updated.
	 * @param scanner The scanner to read user input.
	 */
	private static void updatePassword(Patient patient, Scanner scanner) {
		System.out.print(
				"Enter new Password (must contain at least 8 characters, including uppercase, lowercase, digit, and special character): ");
		String newPassword = scanner.nextLine();

		// Check if the password meets the requirements
		if (!LoginHandler.isValidPassword(newPassword)) {
			System.out.println(
					"Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special character, and be at least 8 characters long.");
			return;
		}

		String hashedNewPassword = LoginHandler.hashPassword(newPassword);
		String currentHashedPassword = LoginHandler.getPasswordHash(patient.getPatientID());

		if (hashedNewPassword.equals(currentHashedPassword)) {
			System.out.println("New password cannot be the same as the current password.");
			return;
		}

		// Update password in LoginHandler
		LoginHandler.changePassword(patient.getPatientID(), newPassword);
		System.out.println("Password updated successfully.");
	}

	/**
	 * Saves the updated information of the patient to the CSV file.
	 *
	 * @param patient The patient whose information is being saved.
	 */
	private static void saveUpdatedPatientInfo(Patient patient) {
		StringBuilder updatedContent = new StringBuilder();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(PATIENT_RECORDS_FILE))) {
			// Read the header and append it
			String header = br.readLine();
			if (header != null) {
				updatedContent.append(header).append("\n");
			}

			// Read and process each line of the CSV
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);

				// Check if the current line matches the patient to update
				if (data[0].equals(patient.getPatientID())) {
					// Update the patient data with the new fields
					updatedContent.append(patient.getPatientID()).append(",").append(patient.getUserName()).append(",")
							.append(patient.getDateOfBirth()).append(",").append(patient.getGender()).append(",")
							.append(patient.getBloodType()).append(",").append(patient.getEmailAddress()).append(",")
							.append(patient.getPhoneNumber()).append("\n");
				} else {
					// Otherwise, just append the existing line
					updatedContent.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write the updated content back to the file
		try (FileWriter writer = new FileWriter(PATIENT_RECORDS_FILE)) {
			writer.write(updatedContent.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
