package sc2002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Administrator extends User {
	private String gender;
	private int age;
	private String emailAddress;
	private String contactNumber;

	public Administrator(String staffID, String name, String gender, int age, String emailAddress, String contactNumber) {
		super(staffID, name, "Administrator");
		this.gender = gender;
		this.age = age;
		this.emailAddress = emailAddress;
		this.contactNumber = contactNumber;
	}

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
					Administrator admin = new Administrator(adminID, adminName, gender, age, emailAddress, contactNumber);
					administrators.put(adminID, admin); // Add to the map
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return administrators;
	}
	
	@Override
	public void accessPortal() {
		System.out.println("Administrator Portal accessed by " + name);
	}

	public String getUserName() {
		return super.name;
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