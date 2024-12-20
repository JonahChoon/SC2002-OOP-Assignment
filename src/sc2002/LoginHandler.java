package sc2002;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 * Handles user authentication and login activities within the system, including
 * password management and activity logging.
 */
public class LoginHandler {

	private static final String LOGIN_DATA_FILE = "data/Master_LoginData.csv";
	private static final String STAFF_RECORDS_FILE = "data/Staff_Records.csv"; // Path to Staff_Records.csv
	private static Map<String, String[]> loginData = new HashMap<>();
	private static Map<String, Boolean> firstLoginData = new HashMap<>();

	/**
	 * Loads login data from the Master_LoginData.csv file into memory.
	 */
	public static void loadLoginData() {
		String line;
		String splitBy = ",";
		try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_DATA_FILE))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length >= 4) {
					String userID = data[0].trim();
					String passwordHash = data[1].trim();
					String role = data[2].trim();
					boolean firstLogin = Boolean.parseBoolean(data[3].trim());

					loginData.put(userID, new String[] { passwordHash, role });
					firstLoginData.put(userID, firstLogin);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Hashes a password using the MD5 algorithm.
	 *
	 * @param password The plain text password to hash.
	 * @return The MD5 hash of the password.
	 */
	public static String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] digest = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves the stored password hash for a given user ID.
	 *
	 * @param userID The user ID whose password hash is to be retrieved.
	 * @return The stored password hash, or null if the user ID does not exist.
	 */
	public static String getPasswordHash(String userID) {
		if (loginData.containsKey(userID)) {
			return loginData.get(userID)[0]; // Returns the password hash
		}
		return null;
	}

	/**
	 * Checks if a user account is deactivated based on the Staff_Records.csv file.
	 *
	 * @param userID The user ID to check for deactivation.
	 * @return {@code true} if the account is deactivated, {@code false} otherwise.
	 */
	private static boolean isAccountDeactivated(String userID) {
		try (BufferedReader br = new BufferedReader(new FileReader(STAFF_RECORDS_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(userID)) {
					return Boolean.parseBoolean(data[7].trim()); // Assuming DEACTIVATED is in the 8th column
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + STAFF_RECORDS_FILE + ": " + e.getMessage());
		}
		return false; // Default to active if user not found
	}

	/**
	 * Authenticates a user by their user ID and password.
	 *
	 * @param userID   The user ID for authentication.
	 * @param password The password for authentication.
	 * @return The role of the user if authentication is successful, or {@code null}
	 *         otherwise.
	 */
	public static String authenticate(String userID, String password) {
		loadLoginData(); // Always load the latest data from CSV

		if (loginData.containsKey(userID)) {
			String[] storedData = loginData.get(userID);
			String storedPasswordHash = storedData[0];
			String role = storedData[1];

			// Check if account is deactivated in Staff_Records.csv
			if (isAccountDeactivated(userID)) {
				System.out.println("Account is deactivated. Please contact the administrator.");
				logActivity(userID, role, "LOGIN", "Failure (Account Deactivated)");
				return null; // Early return if the account is deactivated
			}

			// Verify password if account is active
			if (storedPasswordHash.equals(hashPassword(password))) {
				logActivity(userID, role, "LOGIN", "Success");
				return role;
			} else {
				logActivity(userID, role, "LOGIN", "Failure (Incorrect Password)");
				System.out.println("Login failed. Invalid UserID or Password.");
				return null;
			}
		}

		System.out.println("Login failed. Invalid UserID or Password.");
		logActivity(userID, "Unknown", "LOGIN", "Failure (User Not Found)");
		return null; // Return null if authentication fails
	}

	/**
	 * Displays the login activity log in a formatted table from the
	 * Login_Activity.csv file.
	 */
	public static void viewLoginActivity() {
		try (BufferedReader br = new BufferedReader(new FileReader("data/Login_Activity.csv"))) {
			String line = br.readLine(); // Skip the header row
			String rowFormat = "│ %-19s │ %-8s │ %-13s │ %-10s │ %-30s │\n";

			// Print ASCII table header with rounded corners
			System.out.println(
					"╭─────────────────────┬──────────┬───────────────┬────────────┬────────────────────────────────╮");
			System.out.printf("│ %-19s │ %-8s │ %-13s │ %-10s │ %-30s │\n", "Timestamp", "User ID", "Role", "Activity",
					"Status");
			System.out.println(
					"├─────────────────────┼──────────┼───────────────┼────────────┼────────────────────────────────┤");

			// Read each line in the file and print in formatted table style
			while ((line = br.readLine()) != null) {
				String[] details = line.split(",");
				System.out.printf(rowFormat, details[0], details[1], details[2], details[3], details[4]);
			}

			// Print ASCII table footer
			System.out.println(
					"╰─────────────────────┴──────────┴───────────────┴────────────┴────────────────────────────────╯");

		} catch (IOException e) {
			System.out.println("Error reading Login_Activity.csv: " + e.getMessage());
		}
	}

	/**
	 * Logs a logout activity for a given user.
	 *
	 * @param userID The user ID of the user logging out.
	 * @param role   The role of the user logging out.
	 */
	public static void logout(String userID, String role) {
		// Call this when a user logs out
		logActivity(userID, role, "LOGOUT", "Success");
	}

	/**
	 * Checks if a user is logging in for the first time.
	 *
	 * @param userID The user ID to check.
	 * @return {@code true} if it is the user's first login, {@code false}
	 *         otherwise.
	 */
	public static boolean isFirstLogin(String userID) {
		return firstLoginData.getOrDefault(userID, false);
	}

	/**
	 * Changes the password for a given user and updates the login data.
	 *
	 * @param userID      The user ID whose password is to be changed.
	 * @param newPassword The new password to set.
	 */
	public static void changePassword(String userID, String newPassword) {
		String hashedPassword = hashPassword(newPassword);
		if (loginData.containsKey(userID)) {
			loginData.put(userID, new String[] { hashedPassword, loginData.get(userID)[1] });
			firstLoginData.put(userID, false);
			updateLoginData();
		}
	}

	/**
	 * Authenticates a user and prompts for password change if it is their first
	 * login.
	 *
	 * @param userID   The user ID for authentication.
	 * @param password The password for authentication.
	 * @param scanner  A {@code Scanner} object for reading user input.
	 * @return The role of the user if authentication is successful, or {@code null}
	 *         otherwise.
	 */
	public static String authenticateWithFirstLogin(String userID, String password, Scanner scanner) {
		loadLoginData(); // Ensure the latest data is loaded

		String role = authenticate(userID, password); // Call the original authenticate method
		if (role != null && isFirstLogin(userID)) {
			System.out.println("This is your first login. You are required to change your password.");

			// Prompt for new password with validation until it meets the criteria
			String newPassword;
			while (true) {
				System.out.print(
						"Enter new Password (must contain at least 8 characters, including uppercase, lowercase, digit, and special character): ");
				newPassword = scanner.nextLine().trim();
				if (isValidPassword(newPassword)) {
					break; // Password meets requirements
				}
				System.out.println("Password does not meet complexity requirements.");
			}

			// Change password once valid input is provided
			changePassword(userID, newPassword); // Call the original changePassword
			System.out.println("Password successfully changed.");
			System.out.println("Press ENTER to continue...");
			scanner.nextLine(); // Wait for user confirmation
		}
		return role; // Return the role as usual if authenticated and after changing password if
						// needed
	}

	/**
	 * Updates the login data in the Master_LoginData.csv file.
	 */
	private static void updateLoginData() {
		StringBuilder content = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_DATA_FILE))) {
			String line;
			String splitBy = ",";

			// Read the header and append it without modification
			String header = br.readLine();
			if (header != null) {
				content.append(header).append("\n"); // Append the header line to the content
			}

			// Process the rest of the file, skipping the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);

				// Only modify the row for the current user
				if (loginData.containsKey(data[0])) {
					String[] updatedData = loginData.get(data[0]);
					boolean firstLogin = firstLoginData.get(data[0]);
					content.append(data[0]).append(",").append(updatedData[0]).append(",").append(updatedData[1])
							.append(",").append(firstLogin).append("\n");
				} else {
					content.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write the modified content back to the file
		try (FileWriter writer = new FileWriter(LOGIN_DATA_FILE)) {
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs an activity (e.g., login or logout) with a timestamp to the
	 * Login_Activity.csv file.
	 *
	 * @param userID The user ID performing the action.
	 * @param role   The role of the user.
	 * @param action The action being logged (e.g., "LOGIN", "LOGOUT").
	 * @param status The status of the action (e.g., "Success", "Failure").
	 */
	public static void logActivity(String userID, String role, String action, String status) {
		String logFilePath = "data/Login_Activity.csv";
		File logFile = new File(logFilePath);

		// Ensure the data directory exists
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}

		// Check if the file already exists and has a trailing newline
		boolean fileExists = logFile.exists();
		boolean hasTrailingNewline = true;

		try (RandomAccessFile raf = new RandomAccessFile(logFilePath, "r")) {
			if (fileExists && raf.length() > 0) {
				raf.seek(raf.length() - 1);
				char lastChar = (char) raf.readByte();
				hasTrailingNewline = (lastChar == '\n');
			}
		} catch (IOException e) {
			System.out.println("Error checking file for trailing newline: " + e.getMessage());
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
			// If the file is newly created or doesn’t have a trailing newline, add a
			// newline
			if (!fileExists || !hasTrailingNewline) {
				writer.newLine();
			}

			// If the file is new or empty, write the header first
			if (!fileExists) {
				writer.write("Timestamp,UserID,Role,Action,Status");
				writer.newLine();
			}

			// Write log entry with a timestamp
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			writer.write(String.format("%s,%s,%s,%s,%s", timestamp, userID, role, action, status));
			writer.newLine();

		} catch (IOException e) {
			System.out.println("Error writing to log file: " + e.getMessage());
		}
	}

	/**
	 * Validates a password based on specific security criteria.
	 *
	 * @param password The password to validate.
	 * @return {@code true} if the password meets the criteria, {@code false}
	 *         otherwise.
	 */
	public static boolean isValidPassword(String password) {
		// Check if the password meets the requirements
		String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		Pattern pattern = Pattern.compile(passwordPattern);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
