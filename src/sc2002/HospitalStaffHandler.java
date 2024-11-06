package sc2002;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Provides functionalities to manage hospital staff such as adding, updating,
 * viewing, and deleting staff records.
 */
public class HospitalStaffHandler {

	/**
	 * Displays the menu for managing hospital staff and handles user interaction.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	public static void manageStaffMenu(Scanner scanner) {
		while (true) {
			displayManageStaffMenu();
			System.out.print("\nChoose an option: ");
			int choice;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				continue;
			}

			switch (choice) {
			case 1:
				viewHospitalStaff("data/Staff_Records.csv");
				break;
			case 2:
				addHospitalStaff("data/Staff_Records.csv", "data/Master_LoginData.csv", scanner);
				break;
			case 3:
				updateStaffInformation(scanner);
				break;
			case 4:
				deleteHospitalStaff(scanner);
				break;
			case 5:
				System.out.println("Returning to Administrator Menu...");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
	 * Displays the main menu options for managing hospital staff.
	 */
	public static void displayManageStaffMenu() {
		System.out.println("\n=========================================");
		System.out.println("       Manage Hospital Staff ");
		System.out.println("=========================================");
		System.out.println("→ 1. View Hospital Staff");
		System.out.println("→ 2. Add Hospital Staff");
		System.out.println("→ 3. Update Hospital Staff");
		System.out.println("→ 4. Delete Hospital Staff");
		System.out.println("→ 5. Return to Main Menu");
		System.out.println("=========================================");
	}

	/**
	 * Views and displays the hospital staff from a given CSV file.
	 *
	 * @param csvFilePath The path to the CSV file containing staff records.
	 */
	public static void viewHospitalStaff(String csvFilePath) {
		List<String[]> doctors = new ArrayList<>();
		List<String[]> pharmacists = new ArrayList<>();
		List<String[]> administrators = new ArrayList<>();

		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length >= 8 && data[7].trim().equalsIgnoreCase("FALSE")) { // Check if active
					String role = data[2].trim();
					String[] staffDetails = { data[1].trim() + " [" + data[0].trim() + "]", // Name [ID]
							"Gender: " + data[3].trim(), "Age: " + data[4].trim(), "Email: " + data[5].trim(),
							"Contact: " + data[6].trim() };

					if (role.equalsIgnoreCase("Doctor")) {
						doctors.add(staffDetails);
					} else if (role.equalsIgnoreCase("Pharmacist")) {
						pharmacists.add(staffDetails);
					} else if (role.equalsIgnoreCase("Administrator")) {
						administrators.add(staffDetails);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + csvFilePath + ": " + e.getMessage());
			return;
		}

		displayStaffCategory("Doctor", doctors);
		displayStaffCategory("Pharmacist", pharmacists);
		displayStaffCategory("Administrator", administrators);
	}

	/**
	 * Displays staff details based on their role (e.g., Doctor, Pharmacist,
	 * Administrator).
	 *
	 * @param role      The role of the staff (e.g., "Doctor").
	 * @param staffList The list of staff details.
	 */
	private static void displayStaffCategory(String role, List<String[]> staffList) {
		System.out.println("─────────────────────────────────────────");
		System.out.println("          " + role);
		System.out.println("─────────────────────────────────────────");

		if (staffList.isEmpty()) {
			System.out.println("No " + role.toLowerCase() + " staff found.");
		} else {
			for (String[] staff : staffList) {
				for (String detail : staff) {
					System.out.println(detail);
				}
				System.out.println();
			}
		}
	}

	/**
	 * Adds a new hospital staff member and updates the relevant CSV files.
	 *
	 * @param staffFilePath The path to the staff records CSV file.
	 * @param loginFilePath The path to the login data CSV file.
	 * @param scanner       A {@code Scanner} object for reading user input.
	 */
	public static void addHospitalStaff(String staffFilePath, String loginFilePath, Scanner scanner) {
		System.out.println("=========================================");
		System.out.println("       Add New Hospital Staff ");
		System.out.println("=========================================");

		String fullName = getValidatedInput(scanner, "Full Name: ", "^[a-zA-Z\\s]+$",
				"Name should not contain numbers.");
		String role = getValidatedInput(scanner, "Role (Administrator, Pharmacist, Doctor): ",
				"^(Administrator|Pharmacist|Doctor)$",
				"Invalid role. Choose either Administrator, Pharmacist, or Doctor.");
		String gender = getValidatedInput(scanner, "Gender (Male/Female): ", "^(Male|Female)$",
				"Invalid gender. Enter Male or Female.");
		int age = getValidatedIntInput(scanner, "Age: ", 16, 80, "Invalid age. Enter a number between 16 and 80.");
		String email = getValidatedInput(scanner, "Email Address: ", "^[\\w-\\.]+@[\\w-]+\\.[\\w-]{2,4}$",
				"Invalid email format. Must be in format XXX@XXX.XXX");
		String contactNumber = getValidatedInput(scanner, "Contact Number: ", "^[89]\\d{7}$",
				"Contact number must be 8 digits and start with 8 or 9.");

		String staffID = generateStaffID(role, staffFilePath);

		System.out.println("\nConfirmation:");
		System.out.println("Staff ID: " + staffID);
		System.out.println("Name: " + fullName);
		System.out.println("Role: " + role);
		System.out.println("Gender: " + gender);
		System.out.println("Age: " + age);
		System.out.println("Email: " + email);
		System.out.println("Contact: " + contactNumber);

		String staffData = staffID + "," + fullName + "," + role + "," + gender + "," + age + "," + email + ","
				+ contactNumber + ",FALSE";
		String loginData = staffID + ",5f4dcc3b5aa765d61d8327deb882cf99," + role + ",TRUE";

		System.out.print("\nIs this information correct? (Y/N): ");
		String confirmation = scanner.nextLine().trim();

		if (confirmation.equalsIgnoreCase("Y")) {
			appendToTempFile(staffFilePath, staffData);
			appendToTempFile(loginFilePath, loginData);

			System.out.println("\nNew staff member added successfully!");
		} else {
			System.out.println("Operation canceled.");
		}
	}

	/**
	 * Gets and validates user input based on a regular expression.
	 *
	 * @param scanner      A {@code Scanner} object for reading user input.
	 * @param prompt       The prompt message for the user.
	 * @param regex        The regular expression for validation.
	 * @param errorMessage The error message to display for invalid input.
	 * @return The validated user input.
	 */
	private static String getValidatedInput(Scanner scanner, String prompt, String regex, String errorMessage) {
		String input;
		while (true) {
			System.out.print(prompt);
			input = scanner.nextLine().trim();
			if (Pattern.matches(regex, input)) {
				break;
			} else {
				System.out.println(errorMessage);
			}
		}
		return input;
	}

	/**
	 * Gets and validates integer input within a specified range.
	 *
	 * @param scanner      A {@code Scanner} object for reading user input.
	 * @param prompt       The prompt message for the user.
	 * @param min          The minimum valid value.
	 * @param max          The maximum valid value.
	 * @param errorMessage The error message to display for invalid input.
	 * @return The validated integer input.
	 */
	private static int getValidatedIntInput(Scanner scanner, String prompt, int min, int max, String errorMessage) {
		int input = 0;
		boolean valid = false;
		while (!valid) {
			System.out.print(prompt);
			try {
				input = Integer.parseInt(scanner.nextLine().trim());
				if (input >= min && input <= max) {
					valid = true;
				} else {
					System.out.println(errorMessage);
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			}
		}
		return input;
	}

	/**
	 * Generates a unique staff ID based on the role and existing records.
	 *
	 * @param role          The role of the staff (e.g., "Doctor").
	 * @param staffFilePath The path to the staff records CSV file.
	 * @return A unique staff ID.
	 */
	private static String generateStaffID(String role, String staffFilePath) {
		String prefix;
		int maxID = 0;

		switch (role) {
		case "Doctor":
			prefix = "D";
			break;
		case "Pharmacist":
			prefix = "P";
			break;
		case "Administrator":
			prefix = "A";
			break;
		default:
			throw new IllegalArgumentException("Invalid role provided: " + role);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(staffFilePath))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String staffID = data[0].trim();
				String deactivated = data[7].trim();

				if (staffID.startsWith(prefix) && deactivated.equalsIgnoreCase("FALSE")) {
					try {
						int idNum = Integer.parseInt(staffID.substring(1));
						maxID = Math.max(maxID, idNum);
					} catch (NumberFormatException e) {
						System.out.println("Error parsing ID number: " + staffID);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + staffFilePath + ": " + e.getMessage());
		}

		return prefix + String.format("%03d", maxID + 1);
	}

	/**
	 * Appends new data to a temporary CSV file and replaces the original file.
	 *
	 * @param filePath The path to the original CSV file.
	 * @param data     The data to append.
	 * @return {@code true} if the operation was successful; {@code false}
	 *         otherwise.
	 */
	private static boolean appendToTempFile(String filePath, String data) {
		File originalFile = new File(filePath);
		File tempFile = new File(filePath + "_temp");

		try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true))) {

			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}

			writer.write(data);
			writer.newLine();

		} catch (IOException e) {
			System.out.println("Error writing to temporary file: " + e.getMessage());
			return false;
		}

		if (originalFile.delete() && tempFile.renameTo(originalFile)) {
			return true;
		} else {
			System.out.println("Failed to replace the original file.");
			return false;
		}
	}

	/**
	 * Updates staff information such as email, contact number, or password.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	public static void updateStaffInformation(Scanner scanner) {
		System.out.println("\n=========================================");
		System.out.println("       Update Staff Information");
		System.out.println("=========================================");
		System.out.println("→ 1. Update Staff Email Address");
		System.out.println("→ 2. Update Staff Contact Number");
		System.out.println("→ 3. Update Password");
		System.out.println("→ 4. Return to Main Menu");
		System.out.println("=========================================");

		System.out.print("\nChoose an option: ");
		int choice;
		try {
			choice = Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid number.");
			return;
		}

		switch (choice) {
		case 1:
			updateStaffEmail(scanner);
			break;
		case 2:
			updateStaffContact(scanner);
			break;
		case 3:
			updateStaffPassword(scanner);
			break;
		case 4:
			return;
		default:
			System.out.println("Invalid choice. Please try again.");
		}
	}

	/**
	 * Updates the email address of a specific staff member.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	private static void updateStaffEmail(Scanner scanner) {
		viewHospitalStaff("data/Staff_Records.csv");
		System.out.print("Enter the Staff ID to update email: ");
		String staffID = scanner.nextLine().trim();

		if (!staffIdExists("data/Staff_Records.csv", staffID)) {
			System.out.println("Error: Staff ID " + staffID + " not found.");
			return;
		}

		String newEmail = getValidatedInput(scanner, "Enter new Email Address: ", "^[\\w-\\.]+@[\\w-]+\\.[\\w-]{2,4}$",
				"Invalid email format. Must be in format XXX@XXX.XXX");

		modifyStaffRecord("data/Staff_Records.csv", staffID, 5, newEmail); // 5 is the email column index
		System.out.println("Email updated successfully for Staff ID: " + staffID);
	}

	/**
	 * Updates the contact number of a specific staff member.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	private static void updateStaffContact(Scanner scanner) {
		viewHospitalStaff("data/Staff_Records.csv");
		System.out.print("Enter the Staff ID to update contact: ");
		String staffID = scanner.nextLine().trim();

		if (!staffIdExists("data/Staff_Records.csv", staffID)) {
			System.out.println("Error: Staff ID " + staffID + " not found.");
			return;
		}

		String newContact = getValidatedInput(scanner, "Enter new Contact Number: ", "^[89]\\d{7}$",
				"Contact number must be 8 digits and start with 8 or 9");

		modifyStaffRecord("data/Staff_Records.csv", staffID, 6, newContact); // 6 is the contact number column index
		System.out.println("Contact updated successfully for Staff ID: " + staffID);
	}

	/**
	 * Updates the password of a specific staff member.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	private static void updateStaffPassword(Scanner scanner) {
		viewHospitalStaff("data/Staff_Records.csv");
		System.out.print("Enter the Staff ID to update password: ");
		String staffID = scanner.nextLine().trim();

		if (!staffIdExists("data/Master_LoginData.csv", staffID)) {
			System.out.println("Error: Staff ID " + staffID + " not found.");
			return;
		}

		String newPassword;
		while (true) {
			System.out.print(
					"Enter new Password (must contain at least 8 characters, including uppercase, lowercase, digit, and special character): ");
			newPassword = scanner.nextLine().trim();
			if (LoginHandler.isValidPassword(newPassword)) {
				break;
			} else {
				System.out.println("Password does not meet complexity requirements.");
			}
		}

		// Hash the new password using MD5
		String hashedPassword = LoginHandler.hashPassword(newPassword);

		// Update the CSV file directly for password
		modifyLoginData("data/Master_LoginData.csv", staffID, hashedPassword);
		System.out.println("Password updated successfully for Staff ID: " + staffID);
	}

	/**
	 * Modifies login data in the CSV file for a specific staff member.
	 *
	 * @param filePath        The path to the login data CSV file.
	 * @param staffID         The ID of the staff member.
	 * @param newPasswordHash The new hashed password.
	 */
	private static void modifyLoginData(String filePath, String staffID, String newPasswordHash) {
		File originalFile = new File(filePath);
		File tempFile = new File(filePath + "_temp");

		try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns[0].equals(staffID)) {
					columns[1] = newPasswordHash; // Update the password hash
					line = String.join(",", columns);
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error updating the password for Staff ID " + staffID + ": " + e.getMessage());
		}

		// Replace original file with the updated file
		if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
			System.out.println("Failed to replace the original file.");
		}
	}

	/**
	 * Modifies a specific column in the staff records CSV file.
	 *
	 * @param filePath    The path to the staff records CSV file.
	 * @param staffID     The ID of the staff member.
	 * @param columnIndex The index of the column to update.
	 * @param newValue    The new value for the column.
	 */
	private static void modifyStaffRecord(String filePath, String staffID, int columnIndex, String newValue) {
		File originalFile = new File(filePath);
		File tempFile = new File(filePath + "_temp");

		try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns[0].equals(staffID)) {
					columns[columnIndex] = newValue; // Update the specific column
					line = String.join(",", columns);
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error updating the record for Staff ID " + staffID + ": " + e.getMessage());
		}

		if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
			System.out.println("Failed to replace the original file.");
		}
	}

	/**
	 * Checks if a staff ID exists in a given CSV file.
	 *
	 * @param filePath The path to the CSV file.
	 * @param staffID  The staff ID to check.
	 * @return {@code true} if the staff ID exists; {@code false} otherwise.
	 */
	private static boolean staffIdExists(String filePath, String staffID) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			reader.readLine(); // Skip header
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns[0].trim().equals(staffID)) {
					return true; // Staff ID found
				}
			}
		} catch (IOException e) {
			System.out.println("Error checking Staff ID in " + filePath + ": " + e.getMessage());
		}
		return false; // Staff ID not found
	}

	/**
	 * Deletes (deactivates) a hospital staff record by marking it as deactivated.
	 *
	 * @param scanner A {@code Scanner} object for reading user input.
	 */
	public static void deleteHospitalStaff(Scanner scanner) {
		viewHospitalStaff("data/Staff_Records.csv");
		System.out.print("Enter the Staff ID to deactivate: ");
		String staffID = scanner.nextLine().trim();

		// Check if the Staff ID exists
		if (!staffIdExists("data/Staff_Records.csv", staffID)) {
			System.out.println("Error: Staff ID " + staffID + " not found.");
			return;
		}

		// Confirm deactivation
		System.out.print("Are you sure you want to deactivate Staff ID " + staffID + "? (Y/N): ");
		String confirmation = scanner.nextLine().trim();
		if (!confirmation.equalsIgnoreCase("Y")) {
			System.out.println("Deactivation canceled.");
			return;
		}

		// Prompt for admin ID and password
		System.out.print("Enter your administrator ID for confirmation: ");
		String adminID = scanner.nextLine().trim();

		System.out.print("Enter your administrator password for confirmation: ");
		String adminPassword = scanner.nextLine().trim();

		String adminRole = LoginHandler.authenticate(adminID, adminPassword);

		if (adminRole == null) {
			System.out.println("Incorrect password. Deactivation aborted.");
			return;
		}

		// Proceed with deactivation in the Staff_Records.csv
		deactivateStaffRecord("data/Staff_Records.csv", staffID);
		System.out.println("Staff ID " + staffID + " has been deactivated successfully.");
	}

	/**
	 * Deactivates a staff record in the CSV file.
	 *
	 * @param filePath The path to the staff records CSV file.
	 * @param staffID  The ID of the staff member to deactivate.
	 */
	private static void deactivateStaffRecord(String filePath, String staffID) {
		File originalFile = new File(filePath);
		File tempFile = new File(filePath + "_temp");

		try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] columns = line.split(",");
				if (columns[0].equals(staffID)) {
					columns[7] = "TRUE"; // Update DEACTIVATED field to TRUE
					line = String.join(",", columns);
				}
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error deactivating the record for Staff ID " + staffID + ": " + e.getMessage());
		}

		// Replace original file with the updated file
		if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
			System.out.println("Failed to replace the original file.");
		}
	}

}