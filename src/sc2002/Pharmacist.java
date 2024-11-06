package sc2002;

import java.util.Scanner;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Pharmacist extends User {
	private String gender;
	private int age;
	private String emailAddress;
	private String contactNumber;

	// Constructor
	public Pharmacist(String pharmacistID, String name, String gender, int age, String emailAddress,
			String contactNumber) {
		super(pharmacistID, name, "Pharmacist");
		this.gender = gender;
		this.age = age;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}

	// Display the pharmacist menu and handle user input
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

	// Load pharmacists from CSV
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

	public String getUserName() {
		return super.name;
	}

	@Override
	public void accessPortal() {
		System.out.println("Pharmacist Portal accessed by " + name);
	}
	// Getter for gender
	public String getGender() {
	    return gender;
	}

	// Getter for age
	public int getAge() {
	    return age;
	}

	// Getter for emailAddress
	public String getEmailAddress() {
	    return emailAddress;
	}

	// Getter for contactNumber
	public String getContactNumber() {
	    return contactNumber;
	}

}
