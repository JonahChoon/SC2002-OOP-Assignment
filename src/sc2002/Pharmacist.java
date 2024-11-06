package sc2002;

import java.util.Scanner;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Represents a Pharmacist user in the hospital management system.
 */
public class Pharmacist extends User {
	private String gender;
	private int age;
	private String emailAddress;
	private String contactNumber;

	/**
	 * Constructs a Pharmacist object with the specified details.
	 *
	 * @param pharmacistID  The ID of the pharmacist.
	 * @param name          The name of the pharmacist.
	 * @param gender        The gender of the pharmacist.
	 * @param age           The age of the pharmacist.
	 * @param emailAddress  The email address of the pharmacist.
	 * @param contactNumber The contact number of the pharmacist.
	 */
	public Pharmacist(String pharmacistID, String name, String gender, int age, String emailAddress,
			String contactNumber) {
		super(pharmacistID, name, "Pharmacist");
		this.gender = gender;
		this.age = age;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}

	/**
	 * Displays the pharmacist menu and handles user input for various options.
	 *
	 * @param scanner The scanner to read user input.
	 */
	public void displayMenu(Scanner scanner) {
		boolean sessionActive = true;

		while (sessionActive) {
			MenuHandler.displayPharmacistMenu();

			int choice = 0;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				continue;
			}

			switch (choice) {
			case 1:
				PrescriptionHandler.viewPendingPrescriptions();
				break;
			case 2:
				PrescriptionHandler.updatePrescriptionStatus(scanner);
				break;
			case 3:
				PrescriptionHandler.viewMedicineStock();
				break;
			case 4:
				PrescriptionHandler.submitReplenishmentRequest(scanner, this.getUserID());
				break;
			case 5:
				System.out.println("Logging out...\n");
				LoginHandler.logout(this.getUserID(), "Pharmacist");
				sessionActive = false;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
	 * Loads pharmacists from a CSV file and returns a map of pharmacists.
	 *
	 * @param csvFile The path to the CSV file.
	 * @return A map containing pharmacist IDs as keys and Pharmacist objects as
	 *         values.
	 */
	public static Map<String, Pharmacist> loadPharmacistsFromCSV(String csvFile) {
		Map<String, Pharmacist> pharmacists = new HashMap<>();
		String line = "";
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length == 8) {
					String pharmacistID = data[0].trim(); // Staff ID
					String pharmacistName = data[1].trim(); // Name
					String gender = data[3].trim(); // Gender
					int age = Integer.parseInt(data[4].trim()); // Age
					String emailAddress = data[5].trim(); // Email
					String contactNumber = data[6].trim(); // Contact number

					Pharmacist pharmacist = new Pharmacist(pharmacistID, pharmacistName, gender, age, emailAddress,
							contactNumber);
					pharmacists.put(pharmacistID, pharmacist);
				} else {
					System.out.println("Skipping invalid row in CSV: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pharmacists;
	}

	/**
	 * Gets the name of the pharmacist.
	 *
	 * @return The name of the pharmacist.
	 */
	public String getUserName() {
		return super.name;
	}

	/**
	 * Provides access to the pharmacist portal.
	 */
	@Override
	public void accessPortal() {
		System.out.println("Pharmacist Portal accessed by " + name);
	}

	/**
	 * Gets the gender of the pharmacist.
	 *
	 * @return The gender of the pharmacist.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Gets the age of the pharmacist.
	 *
	 * @return The age of the pharmacist.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Gets the email address of the pharmacist.
	 *
	 * @return The email address of the pharmacist.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Gets the contact number of the pharmacist.
	 *
	 * @return The contact number of the pharmacist.
	 */
	public String getContactNumber() {
		return contactNumber;
	}

}
