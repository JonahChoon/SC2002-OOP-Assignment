package sc2002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents an Administrator user who can manage various tasks within the
 * system. Extends the {@code User} class to inherit common user properties and
 * methods.
 */
public class Administrator extends User {
	private String gender;
	private int age;
	private String emailAddress;
	private String contactNumber;

	/**
	 * Constructor to initialise an Administrator object.
	 *
	 * @param staffID       The staff ID of the administrator.
	 * @param name          The name of the administrator.
	 * @param gender        The gender of the administrator.
	 * @param age           The age of the administrator.
	 * @param emailAddress  The email address of the administrator.
	 * @param contactNumber The contact number of the administrator.
	 */
	public Administrator(String staffID, String name, String gender, int age, String emailAddress,
			String contactNumber) {
		super(staffID, name, "Administrator");
		this.gender = gender;
		this.age = age;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}

	/**
	 * Displays the menu for the administrator and handles input actions.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	public void displayMenu(Scanner scanner) {
		boolean sessionActive = true;

		while (sessionActive) {
			MenuHandler.displayAdministratorMenu();

			int choice = 0;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				continue;
			}

			switch (choice) {
			case 1:
				HospitalStaffHandler.manageStaffMenu(scanner);
				break;
			case 2:
				AppointmentSlotHandler.viewAllAppointmentsAndOutcomesForAdmin(scanner);
				break;
			case 3:
				PrescriptionHandler.viewMedicineStock();
				System.out.print("\n");
				PrescriptionHandler.viewAllMedicineRequests();
				break;
			case 4:
				PrescriptionHandler.viewMedicineStock();
				System.out.print("\n");
				PrescriptionHandler.approveReplenishmentRequests(scanner, this.getUserID());
				break;
			case 5:
				LoginHandler.viewLoginActivity();
				break;
			case 6:
				System.out.println("Logging out...\n");
				LoginHandler.logout(this.getUserID(), "Administrator");
				sessionActive = false;
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
	 * Loads a list of administrators from a CSV file.
	 *
	 * @param csvFile The path to the CSV file containing administrator data.
	 * @return A map of administrator objects with their IDs as keys.
	 */
	public static Map<String, Administrator> loadAdministratorsFromCSV(String csvFile) {
		Map<String, Administrator> administrators = new HashMap<>();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length == 8) {
					String adminID = data[0].trim(); // Staff ID
					String adminName = data[1].trim(); // Name
					String gender = data[3].trim(); // Gender
					int age = Integer.parseInt(data[4].trim()); // Age
					String emailAddress = data[5].trim(); // Email
					String contactNumber = data[6].trim(); // Contact number

					// Create the Administrator object
					Administrator admin = new Administrator(adminID, adminName, gender, age, emailAddress,
							contactNumber);
					administrators.put(adminID, admin); // Add to the map
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return administrators;
	}

	/**
	 * Accesses the administrator portal and prints a message indicating that the
	 * portal has been accessed by the administrator.
	 */
	@Override
	public void accessPortal() {
		System.out.println("Administrator Portal accessed by " + name);
	}

	/**
	 * Retrieves the name of the user.
	 *
	 * @return the name of the user as a String.
	 */
	public String getUserName() {
		return super.name;
	}

	/**
	 * Retrieves the gender of the user.
	 *
	 * @return the gender of the user as a String.
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Retrieves the age of the user.
	 *
	 * @return the age of the user as an integer.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Retrieves the email address of the user.
	 *
	 * @return the email address of the user as a String.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Retrieves the contact number of the user.
	 *
	 * @return the contact number of the user as a String.
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * Sets the gender of the user.
	 *
	 * @param gender the gender to be set for the user.
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Sets the age of the user.
	 *
	 * @param age the age to be set for the user.
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Sets the email address of the user.
	 *
	 * @param emailAddress the email address to be set for the user.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Sets the contact number of the user.
	 *
	 * @param contactNumber the contact number to be set for the user.
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
}