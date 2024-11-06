package sc2002;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class AppointmentSlotHandler {

	private static final String AVAILABILITY_FILE = "data/Doctor_Availability.csv";
	private static final String STAFF_RECORDS_FILE = "data/Staff_Records.csv";
	private static final String APPOINTMENT_FILE = "data/Patient_Appointment.csv";
	private static final String DIAGNOSIS_FILE = "data/Patient_Diagnosis_Prescription.csv";
	private static final String MEDICINE_FILE = "data/Medicine_Stock.csv";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

	// Function to view available slots (remains unchanged)
	public static void viewAvailableAppointmentSlots(Scanner scanner) {
		Map<String, DoctorAvailability> doctorAvailabilities = loadDoctorAvailability();
		Map<String, String> doctorNames = loadDoctorNames();

		// Sort doctor IDs in logical order (e.g., D001, D002)
		List<String> sortedDoctorIDs = new ArrayList<>(doctorAvailabilities.keySet());
		Collections.sort(sortedDoctorIDs);

		System.out.println("\n=========================================");
		System.out.println("    View Available Appointment Slots");
		System.out.println("=========================================");

		if (doctorAvailabilities.isEmpty()) {
			System.out.println("No available appointment slots found.");
			return; // Return to the main menu
		}

		displayDoctorAvailability(doctorAvailabilities, doctorNames, sortedDoctorIDs);

		System.out.print("\nPlease select the Doctor ID and Date (Format: DXXX DD/MM/YY): ");
		String input = scanner.nextLine();
		String pattern = "^D\\d{3}\\s\\d{2}/\\d{2}/\\d{2}$";
		if (!Pattern.matches(pattern, input)) {
			System.out.println("Invalid input format. Please use DXXX DD/MM/YY format.");
			return; // Return to the main menu
		}

		String[] parts = input.split(" ");
		String selectedDoctorID = parts[0].trim();
		String selectedDate = parts[1].trim();

		DoctorAvailability selectedDoctor = doctorAvailabilities.get(selectedDoctorID);
		if (selectedDoctor == null || !selectedDoctor.getAvailableDates().containsKey(selectedDate)) {
			System.out.println("Invalid Doctor ID or Date selected.");
			return; // Return to the main menu
		}

		List<String> availableTimeSlots = selectedDoctor.getAvailableDates().get(selectedDate);
		if (availableTimeSlots.isEmpty()) {
			System.out.println("No time slots available for the selected date.");
		} else {
			displayTimeSlots(selectedDoctor, selectedDate, availableTimeSlots, doctorNames);
		}
	}

	public static void scheduleAppointment(Scanner scanner, Patient patient) {
		// Load doctor availability and names
		Map<String, DoctorAvailability> doctorAvailabilities = loadDoctorAvailability();
		Map<String, String> doctorNames = loadDoctorNames();

		// Sort doctor IDs in logical order (e.g., D001, D002)
		List<String> sortedDoctorIDs = new ArrayList<>(doctorAvailabilities.keySet());
		Collections.sort(sortedDoctorIDs);

		// Display available slots only once
		displayDoctorAvailability(doctorAvailabilities, doctorNames, sortedDoctorIDs);

		// Prompt for Doctor ID and Date to schedule
		System.out.print("\nPlease select the Doctor ID and Date to schedule (Format: DXXX DD/MM/YY): ");
		String input = scanner.nextLine();
		String pattern = "^D\\d{3}\\s\\d{2}/\\d{2}/\\d{2}$";
		if (!Pattern.matches(pattern, input)) {
			System.out.println("Invalid input format. Please use DXXX DD/MM/YY format.");
			return;
		}

		String[] parts = input.split(" ");
		String selectedDoctorID = parts[0].trim();
		String selectedDate = parts[1].trim();

		// Check if the doctor and date are available
		DoctorAvailability selectedDoctor = doctorAvailabilities.get(selectedDoctorID);
		if (selectedDoctor == null || !selectedDoctor.getAvailableDates().containsKey(selectedDate)) {
			System.out.println("Invalid Doctor ID or Date selected.");
			return;
		}

		// Show available time slots
		List<String> availableTimeSlots = selectedDoctor.getAvailableDates().get(selectedDate);
		if (availableTimeSlots.isEmpty()) {
			System.out.println("No time slots available for the selected date.");
			return;
		} else {
			displayTimeSlots(selectedDoctor, selectedDate, availableTimeSlots, doctorNames);
		}

		// Allow user to select a time slot
		System.out.print("Select a time slot number to schedule: ");
		String selectedSlot = scanner.nextLine();
		try {
			int slotNumber = Integer.parseInt(selectedSlot);
			if (slotNumber >= 1 && slotNumber <= availableTimeSlots.size()) {
				String confirmedTimeSlot = availableTimeSlots.get(slotNumber - 1);

				// Save the appointment in Patient_Appointment.csv
				saveAppointment(patient, selectedDoctorID, selectedDate, confirmedTimeSlot);

				// Update the status in Doctor_Availability.csv to "Unavailable"
				updateDoctorAvailability(selectedDoctorID, selectedDate, confirmedTimeSlot);

				System.out.println("\nAppointment scheduled successfully.\n");
			} else {
				System.out.println("Invalid selection. Returning to the main menu...");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid number.");
		}
	}

	private static void saveAppointment(Patient patient, String doctorID, String date, String timeSlot) {
		String newAppointmentID = generateAppointmentID();
		String status = "Pending";

		String[] appointmentDetails = { newAppointmentID, patient.getPatientID(), doctorID, date, timeSlot, status,
				"-" };

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Appointment.csv", true))) {
			File appointmentFile = new File("data/Patient_Appointment.csv");

			// Check if the file is not empty and if the last line has a trailing newline
			if (appointmentFile.length() > 0) {
				try (RandomAccessFile fileHandler = new RandomAccessFile(appointmentFile, "r")) {
					fileHandler.seek(appointmentFile.length() - 1);
					int lastChar = fileHandler.read();
					if (lastChar != '\n') {
						writer.newLine(); // Add a newline if not present
					}
				}
			}

			// Append the new appointment details
			writer.append(String.join(",", appointmentDetails));
			writer.newLine(); // Ensure the new entry is in a new row
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String generateAppointmentID() {
		String lastAppointmentID = "";
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				lastAppointmentID = data[0].trim(); // Get the last appointment ID
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (lastAppointmentID.isEmpty()) {
			return "AP001";
		}

		int lastNumber = Integer.parseInt(lastAppointmentID.substring(2));
		int newNumber = lastNumber + 1;
		return String.format("AP%03d", newNumber);
	}

	// Load Doctor Availability (same as your current implementation)
	private static Map<String, DoctorAvailability> loadDoctorAvailability() {
		Map<String, DoctorAvailability> doctorAvailabilityMap = new HashMap<>();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(AVAILABILITY_FILE))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length == 4) {
					String doctorID = data[0].trim();
					String date = data[1].trim();
					String timeSlot = data[2].trim();
					String status = data[3].trim();
					if (status.equalsIgnoreCase("Available")) {
						doctorAvailabilityMap.putIfAbsent(doctorID, new DoctorAvailability(doctorID));
						DoctorAvailability doctorAvailability = doctorAvailabilityMap.get(doctorID);
						doctorAvailability.addAvailability(date, timeSlot);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doctorAvailabilityMap;
	}

	// Load Doctor Names (same as your current implementation)
	private static Map<String, String> loadDoctorNames() {
		Map<String, String> doctorNames = new HashMap<>();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(STAFF_RECORDS_FILE))) {
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length >= 2) {
					String doctorID = data[0].trim();
					String doctorName = data[1].trim();
					doctorNames.put(doctorID, doctorName); // Map doctorID to doctorName
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doctorNames;
	}

	// Display Doctor Availability (same as your current implementation)
	private static void displayDoctorAvailability(Map<String, DoctorAvailability> doctorAvailabilityMap,
			Map<String, String> doctorNames, List<String> sortedDoctorIDs) {
		for (String doctorID : sortedDoctorIDs) {
			DoctorAvailability doctorAvailability = doctorAvailabilityMap.get(doctorID);
			String doctorName = doctorNames.getOrDefault(doctorAvailability.getDoctorID(), "Unknown Doctor");
			String separator = "─────────────────────────────────────────";
			System.out.println("\n" + separator);
			System.out.printf("%s [%s]%n", doctorName, doctorAvailability.getDoctorID());
			System.out.println(separator);

			// Get the available dates and sort them
			List<LocalDate> sortedDates = new ArrayList<>();
			for (String date : doctorAvailability.getAvailableDates().keySet()) {
				try {
					LocalDate parsedDate = LocalDate.parse(date, DATE_FORMATTER);
					sortedDates.add(parsedDate);
				} catch (DateTimeParseException e) {
					System.out.println("Invalid date format found: " + date);
				}
			}
			sortedDates.sort(Comparator.naturalOrder()); // Sort dates in ascending order

			for (LocalDate date : sortedDates) {
				System.out.printf("☞ Available Date: %s%n", date.format(DATE_FORMATTER));
			}
		}
	}

	// Display Time Slots (same as your current implementation)
	private static void displayTimeSlots(DoctorAvailability doctorAvailability, String date, List<String> timeSlots,
			Map<String, String> doctorNames) {
		String doctorName = doctorNames.getOrDefault(doctorAvailability.getDoctorID(), "Unknown Doctor");
		String separator = "─────────────────────────────────────────";
		System.out.println("\n" + separator);
		System.out.printf("%s [%s]%n", doctorName, doctorAvailability.getDoctorID());
		System.out.println(separator);

		System.out.println("Available Time Slots for " + date + ":");
		for (int i = 0; i < timeSlots.size(); i++) {
			System.out.printf("→ %d. %s%n", i + 1, timeSlots.get(i));
		}
		System.out.println();
	}

	// Inner class to handle doctor availability
	private static class DoctorAvailability {
		private String doctorID;
		private Map<String, List<String>> availableDates;

		public DoctorAvailability(String doctorID) {
			this.doctorID = doctorID;
			this.availableDates = new HashMap<>();
		}

		public void addAvailability(String date, String timeSlot) {
			availableDates.putIfAbsent(date, new ArrayList<>());
			availableDates.get(date).add(timeSlot);
		}

		public String getDoctorID() {
			return doctorID;
		}

		public Map<String, List<String>> getAvailableDates() {
			return availableDates;
		}
	}

	private static void updateDoctorAvailability(String doctorID, String date, String timeSlot) {
		StringBuilder updatedContent = new StringBuilder();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader("data/Doctor_Availability.csv"))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data[0].trim().equals(doctorID) && data[1].trim().equals(date) && data[2].trim().equals(timeSlot)) {
					// Update the status to "Unavailable"
					data[3] = "Unavailable";
					updatedContent.append(String.join(",", data)).append("\n");
				} else {
					updatedContent.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write the updated content back to the file
		try (FileWriter writer = new FileWriter("data/Doctor_Availability.csv")) {
			writer.write(updatedContent.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void viewAppointments(Scanner scanner, Patient patient) {
		String patientID = patient.getPatientID(); // Get the current patient's ID
		List<String[]> appointments = new ArrayList<>();

		// Read appointments from the Patient_Appointment.csv file
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String appointmentID = data[0].trim();
				String appointmentPatientID = data[1].trim();
				String doctorID = data[2].trim();
				String date = data[3].trim();
				String timeSlot = data[4].trim();
				String status = data[5].trim();
				String outcome = data[6].trim();

				// Check if the appointment belongs to the current patient and
				// Status is "Confirmed" or "Pending" AND outcome is "-"
				if (appointmentPatientID.equals(patientID)
						&& (status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Pending"))
						&& outcome.equals("-")) {
					appointments.add(new String[] { appointmentID, doctorID, date, timeSlot, status, outcome });
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Check if any appointments are found
		if (appointments.isEmpty()) {
			System.out.println("\nNo upcoming appointments found for patient: " + patient.getUserName());
			return;
		}

		// Display the appointments
		Map<String, String> doctorNames = loadDoctorNames(); // Load doctor names from the staff records

		String separator = "─────────────────────────────────────────";
		System.out.println("\n=========================================");
		System.out.println("         View Upcoming Appointments");
		System.out.println("=========================================\n");

		for (String[] appointment : appointments) {
			String appointmentID = appointment[0];
			String doctorID = appointment[1];
			String appointmentDate = appointment[2];
			String timeSlot = appointment[3];
			String status = appointment[4];

			String doctorName = doctorNames.getOrDefault(doctorID, "Unknown Doctor");

			System.out.println(separator);
			System.out.printf("Appointment ID   : %s%n", appointmentID);
			System.out.printf("Doctor           : %s [%s]%n", doctorName, doctorID);
			System.out.printf("Date             : %s%n", appointmentDate);
			System.out.printf("Time Slot        : %s%n", timeSlot);
			System.out.printf("Status           : %s%n", status);
			System.out.print("\n");
		}

	}

	public static void rescheduleAppointment(Scanner scanner, Patient patient) {
		// Step 1: Display the patient's upcoming appointments
		viewAppointments(scanner, patient);

		// Step 2: Prompt the user to select an appointment to reschedule by its
		// Appointment ID
		System.out.println("─────────────────────────────────────────");
		System.out.print("\nEnter the Appointment ID you want to reschedule: ");
		String appointmentIDToReschedule = scanner.nextLine().trim();

		// Step 3: Retrieve the details of the selected appointment from
		// Patient_Appointment.csv
		String[] selectedAppointment = null;
		String oldDoctorID = null;
		String oldDate = null;
		String oldTimeSlot = null;

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(appointmentIDToReschedule)) {
					selectedAppointment = data;
					oldDoctorID = data[2].trim();
					oldDate = data[3].trim();
					oldTimeSlot = data[4].trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (selectedAppointment == null) {
			System.out.println("Invalid Appointment ID.");
			return;
		}

		// Step 4: Use scheduleAppointment logic to prompt for the new Doctor, Date, and
		// Time
		System.out.println("Rescheduling appointment...");
		String selectedDoctorID = oldDoctorID;
		String newDate = oldDate;
		String newTimeSlot = oldTimeSlot;

		// Prompt for a new date and time using the same logic as scheduleAppointment
		Map<String, DoctorAvailability> doctorAvailabilities = loadDoctorAvailability();
		Map<String, String> doctorNames = loadDoctorNames();

		// Display doctor availability and ask for a new schedule
		List<String> sortedDoctorIDs = new ArrayList<>(doctorAvailabilities.keySet());
		Collections.sort(sortedDoctorIDs);

		// Debug: Display all available doctor IDs and dates
		System.out.println("\nAvailable Doctors and Dates:");
		for (String doctorID : sortedDoctorIDs) {
			DoctorAvailability doctorAvailability = doctorAvailabilities.get(doctorID);
			String doctorName = doctorNames.getOrDefault(doctorID, "Unknown Doctor"); // Fetch doctor name based on
																						// doctorID
			String separator = "─────────────────────────────────────────";

			// Display doctor name and ID
			System.out.println("\n" + separator);
			System.out.printf("%s [%s]%n", doctorName, doctorID);
			System.out.println(separator);

			List<LocalDate> sortedDates = new ArrayList<>();
			for (String date : doctorAvailability.getAvailableDates().keySet()) {
				try {
					// Parse the date from String to LocalDate using the DATE_FORMATTER
					LocalDate parsedDate = LocalDate.parse(date, DATE_FORMATTER);
					sortedDates.add(parsedDate);
				} catch (DateTimeParseException e) {
					System.out.println("Invalid date format found: " + date);
				}
			}

			// Sort the dates in natural ascending order
			sortedDates.sort(Comparator.naturalOrder());

			// Display the sorted dates
			for (LocalDate date : sortedDates) {
				// Format the date back to String for display
				String formattedDate = date.format(DATE_FORMATTER);
				System.out.printf("☞ Available Date: %s%n", formattedDate);
			}
		}

		// Ask for user input for rescheduling
		System.out.print("\nPlease select the Doctor ID and Date to reschedule (Format: DXXX DD/MM/YY): ");
		String input = scanner.nextLine();
		String pattern = "^D\\d{3}\\s\\d{2}/\\d{2}/\\d{2}$";
		if (!Pattern.matches(pattern, input)) {
			System.out.println("Invalid input format. Please use DXXX DD/MM/YY format.");
			return;
		}

		String[] parts = input.split(" ");
		selectedDoctorID = parts[0].trim();
		newDate = parts[1].trim();

		// Check if the doctor and date are available
		DoctorAvailability selectedDoctor = doctorAvailabilities.get(selectedDoctorID);

		// Debug: Check if doctor and date exist in the system
		if (selectedDoctor == null) {
			System.out.println("Doctor ID not found: " + selectedDoctorID);
			return;
		}

		if (!selectedDoctor.getAvailableDates().containsKey(newDate)) {
			System.out.println("Date not found for Doctor ID: " + selectedDoctorID + ", Date: " + newDate);
			return;
		}

		// Proceed if doctor and date are valid
		List<String> availableTimeSlots = selectedDoctor.getAvailableDates().get(newDate);
		if (availableTimeSlots.isEmpty()) {
			System.out.println("No time slots available for the selected date.");
			return;
		} else {
			displayTimeSlots(selectedDoctor, newDate, availableTimeSlots, doctorNames);
		}

		// Allow the user to select a new time slot
		System.out.print("Select a time slot number to reschedule: ");
		String selectedSlot = scanner.nextLine();
		try {
			int slotNumber = Integer.parseInt(selectedSlot);
			if (slotNumber >= 1 && slotNumber <= availableTimeSlots.size()) {
				newTimeSlot = availableTimeSlots.get(slotNumber - 1);
			} else {
				System.out.println("Invalid selection. Returning to the main menu...");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid number.");
			return;
		}

		// Step 5: Update the Patient_Appointment.csv with the new details (keeping the
		// same Appointment ID)
		updateAppointment(appointmentIDToReschedule, patient.getPatientID(), selectedDoctorID, newDate, newTimeSlot);

		// Step 6: Update the Doctor_Availability.csv
		// Mark the old time slot as "Available"
		updateDoctorAvailability(oldDoctorID, oldDate, oldTimeSlot, "Available");

		// Mark the new time slot as "Unavailable"
		updateDoctorAvailability(selectedDoctorID, newDate, newTimeSlot, "Unavailable");

		System.out.println("\nAppointment rescheduled successfully.");
	}

	private static void updateAppointment(String appointmentID, String patientID, String doctorID, String date,
			String timeSlot) {
		StringBuilder updatedContent = new StringBuilder(); // To store the updated file content
		boolean appointmentUpdated = false; // Flag to track if we updated the row
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);

				// Check if the appointment ID matches the one to be updated
				if (data[0].trim().equals(appointmentID)) {
					data[2] = doctorID; // Update Doctor ID
					data[3] = date; // Update Appointment Date
					data[4] = timeSlot; // Update Appointment Time Slot
					data[5] = "Pending"; // Set status back to "Pending"
					data[6] = "-"; // Outcome remains "-"

					// Append the updated row
					updatedContent.append(String.join(",", data)).append("\n");
					appointmentUpdated = true; // Mark the appointment as updated
				} else {
					// Append unmodified rows to the content
					updatedContent.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!appointmentUpdated) {
			System.out.println("Appointment ID not found.");
			return;
		}

		// Overwrite the file with the updated content
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Appointment.csv", false))) {
			writer.write(updatedContent.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Appointment ID " + appointmentID + " has been successfully updated.");
	}

	private static void updateDoctorAvailability(String doctorID, String date, String timeSlot, String newStatus) {
		StringBuilder updatedContent = new StringBuilder();
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader("data/Doctor_Availability.csv"))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data[0].trim().equals(doctorID) && data[1].trim().equals(date) && data[2].trim().equals(timeSlot)) {
					// Update the status to "Available" or "Unavailable"
					data[3] = newStatus;
					updatedContent.append(String.join(",", data)).append("\n");
				} else {
					updatedContent.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write the updated content back to the file
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Doctor_Availability.csv", false))) {
			writer.write(updatedContent.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cancelAppointment(Scanner scanner, Patient patient) {
		// Step 1: Display the patient's upcoming appointments
		viewAppointments(scanner, patient);

		// Step 2: Prompt the user to select an appointment to cancel by its Appointment
		// ID
		System.out.println("─────────────────────────────────────────");
		System.out.print("\nEnter the Appointment ID you want to cancel: ");
		String appointmentIDToCancel = scanner.nextLine().trim();

		// Step 3: Retrieve the details of the selected appointment from
		// Patient_Appointment.csv
		String[] selectedAppointment = null;
		String doctorID = null;
		String date = null;
		String timeSlot = null;

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(appointmentIDToCancel)) {
					selectedAppointment = data;
					doctorID = data[2].trim(); // Doctor ID
					date = data[3].trim(); // Appointment Date
					timeSlot = data[4].trim(); // Appointment Time Slot
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (selectedAppointment == null) {
			System.out.println("Invalid Appointment ID.");
			return;
		}

		// Step 4: Update Patient_Appointment.csv with the status "Cancelled by Patient"
		updateAppointmentCancellation(appointmentIDToCancel, patient.getPatientID());

		// Step 5: Update Doctor_Availability.csv to mark the time slot as "Available"
		updateDoctorAvailability(doctorID, date, timeSlot, "Available");

		System.out.println("\nAppointment cancelled successfully.");
	}

	// Helper method to update appointment status in Patient_Appointment.csv
	private static void updateAppointmentCancellation(String appointmentID, String patientID) {
		StringBuilder updatedContent = new StringBuilder(); // To store the updated file content
		boolean appointmentUpdated = false; // Flag to track if the appointment was updated
		String line;
		String splitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);

				// Check if the appointment ID matches the one to be updated
				if (data[0].trim().equals(appointmentID)) {
					data[5] = "Cancelled by Patient"; // Update the status to "Cancelled by Patient"
					// Outcome remains "-", we don't need to change that
					updatedContent.append(String.join(",", data)).append("\n");
					appointmentUpdated = true; // Mark the appointment as updated
				} else {
					// Append unmodified rows to the content
					updatedContent.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!appointmentUpdated) {
			System.out.println("Appointment ID not found.");
			return;
		}

		// Overwrite the file with the updated content
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Appointment.csv", false))) {
			writer.write(updatedContent.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Appointment ID " + appointmentID + " has been successfully cancelled.");
	}

	// Helper function to load doctor names from Staff_Records.csv
	private static String loadDoctorName(String doctorID) {
		Map<String, String> doctorNames = loadDoctorNames(); // Use existing function to load doctor names
		return doctorNames.getOrDefault(doctorID, "Unknown Doctor");
	}

	private static Map<String, String[]> loadDiagnosisDetails(String patientID) {
		Map<String, String[]> diagnosisMap = new HashMap<>(); // Store diagnosis details mapped by Appointment ID

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line = br.readLine(); // Skip header

			// Read each line of the CSV file
			while ((line = br.readLine()) != null) {

				// Split the line by commas to extract individual fields
				String[] data = line.split(",");

				// Check if the line matches the given patient ID
				if (data[0].trim().equals(patientID)) {
					// Extract details
					String appointmentID = data[9].trim(); // The Appointment ID
					String diagnosis = data[2].trim(); // Diagnosis
					String treatment = data[3].trim(); // Treatment
					String prescription = data[4].trim(); // Prescription
					String quantity = data[5].trim(); // Quantity
					String caseNotes = data[6].trim(); // Case Notes
					String typeOfService = data[10].trim(); // Type of Service

					// Add to the map using Appointment ID as the key
					diagnosisMap.put(appointmentID,
							new String[] { diagnosis, treatment, prescription, quantity, caseNotes, typeOfService });
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return diagnosisMap; // Return the diagnosis details mapped by Appointment ID
	}

	public static void viewAllAppointmentsAndOutcomes(Scanner scanner, Patient patient) {
		String patientID = patient.getPatientID().trim(); // Get the current patient's ID and trim whitespace
		List<String[]> upcomingAppointments = new ArrayList<>();
		List<String[]> cancelledAppointments = new ArrayList<>();
		List<String[]> completedAppointments = new ArrayList<>();
		Map<String, String[]> diagnosisMap = loadDiagnosisDetails(patientID); // Load diagnosis details

		// Read appointments from Patient_Appointment.csv
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line = br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String appointmentID = data[0].trim();
				String appointmentPatientID = data[1].trim(); // Extract patient ID from the CSV
				String doctorID = data[2].trim();
				String date = data[3].trim();
				String timeSlot = data[4].trim();
				String status = data[5].trim();
				String outcome = data[6].trim();

				// Ensure the record belongs to the current patient
				if (appointmentPatientID.equals(patientID)) {
					if ((status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Pending"))
							&& outcome.equals("-")) {
						upcomingAppointments.add(new String[] { appointmentID, doctorID, date, timeSlot, status });
					} else if (status.contains("Cancelled")) {
						cancelledAppointments.add(new String[] { appointmentID, doctorID, date, timeSlot, status });
					} else if (outcome.equalsIgnoreCase("Completed")) {
						completedAppointments.add(new String[] { appointmentID, doctorID, date, timeSlot, status });
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Display Upcoming Appointments
		System.out.println("=========================================");
		System.out.println("         Upcoming Appointments");
		System.out.println("=========================================");
		displayAppointmentList(upcomingAppointments);

		// Display Cancelled Appointments
		System.out.println("=========================================");
		System.out.println("         Cancelled Appointments");
		System.out.println("=========================================");
		displayAppointmentList(cancelledAppointments);

		// Display Completed Appointments
		System.out.println("=========================================");
		System.out.println("         Completed Appointments");
		System.out.println("=========================================");
		displayCompletedAppointments(completedAppointments, diagnosisMap);
	}

	private static void displayAppointmentList(List<String[]> appointments) {
		for (String[] appointment : appointments) {
			String appointmentID = appointment[0];
			String doctorID = appointment[1];
			String appointmentDate = appointment[2];
			String timeSlot = appointment[3];
			String status = appointment[4];
			String doctorName = loadDoctorName(doctorID); // Get doctor name by ID

			printAppointmentDetails(appointmentID, doctorName, doctorID, appointmentDate, timeSlot, status);
		}
	}

	private static void displayCompletedAppointments(List<String[]> appointments, Map<String, String[]> diagnosisMap) {
		for (String[] appointment : appointments) {
			String appointmentID = appointment[0];
			String doctorID = appointment[1];
			String appointmentDate = appointment[2];
			String timeSlot = appointment[3];
			String status = appointment[4];
			String doctorName = loadDoctorName(doctorID);

			printAppointmentDetails(appointmentID, doctorName, doctorID, appointmentDate, timeSlot, status);

			if (diagnosisMap.containsKey(appointmentID)) {
				String[] diagnosisDetails = diagnosisMap.get(appointmentID);
				System.out.printf("Diagnosis        : %s%n", diagnosisDetails[0]);
				System.out.printf("Treatment        : %s%n", diagnosisDetails[1]);
				System.out.printf("Prescription     : %s%n", diagnosisDetails[2]);
				System.out.printf("Quantity         : %s%n", diagnosisDetails[3]);
				System.out.printf("Case Notes       : %s%n", diagnosisDetails[4]);
				System.out.printf("Type of Service  : %s%n", diagnosisDetails[5] + "\n");
			} else {
				System.out.println("No diagnosis details found.");
			}
		}
	}

	private static void printAppointmentDetails(String appointmentID, String doctorName, String doctorID,
			String appointmentDate, String timeSlot, String status) {
		System.out.printf("Appointment ID   : %s%n", appointmentID);
		System.out.printf("Doctor           : %s [%s]%n", doctorName, doctorID);
		System.out.printf("Date             : %s%n", appointmentDate);
		System.out.printf("Time Slot        : %s%n", timeSlot);
		System.out.printf("Status           : %s%n", status + "\n");
	}

	public static void viewUpcomingAppointments(String doctorID) {
		List<String[]> upcomingAppointments = new ArrayList<>();
		Map<String, String> patientNames = new HashMap<>();

		// Load patient names for displaying in the appointment list
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Records.csv"))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length >= 2) {
					String patientID = data[0].trim();
					String patientName = data[1].trim();
					patientNames.put(patientID, patientName);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Records.csv: " + e.getMessage());
			return;
		}

		// Read from Patient_Appointment.csv to find upcoming appointments for the given
		// doctor
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
			String line;
			br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String appointmentDoctorID = data[2].trim();
				String status = data[5].trim();
				String outcome = data[6].trim();

				// Find appointments that are confirmed and not yet completed
				if (appointmentDoctorID.equals(doctorID) && status.equalsIgnoreCase("Confirmed")
						&& outcome.equals("-")) {
					upcomingAppointments.add(data);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Appointment.csv: " + e.getMessage());
			return;
		}

		// Check if any upcoming appointments exist
		if (upcomingAppointments.isEmpty()) {
			System.out.println("No upcoming appointments found for Doctor ID: " + doctorID);
			return;
		}

		// Display the upcoming appointments
		System.out.println("=========================================");
		System.out.println("         Upcoming Appointment Slot       ");
		System.out.println("=========================================\n");

		for (String[] appointment : upcomingAppointments) {
			String appointmentID = appointment[0].trim();
			String patientID = appointment[1].trim();
			String date = appointment[3].trim();
			String timeSlot = appointment[4].trim();
			String patientName = patientNames.getOrDefault(patientID, "Unknown Patient");

			System.out.println("─────────────────────────────────────────");
			System.out.printf("         Appointment ID: %s%n", appointmentID);
			System.out.println("─────────────────────────────────────────");
			System.out.printf("%s [%s]%n", patientName, patientID);
			System.out.printf("Date : %s%n", date);
			System.out.printf("Time : %s%n", timeSlot);
			System.out.println();
		}
	}

	public static void recordAppointmentOutcome(String doctorID, Scanner scanner) {
		// Step 1: Display upcoming appointments for the doctor
		List<String[]> upcomingAppointments = new ArrayList<>();
		Map<String, String> patientNames = loadPatientNames();

		// Read from Patient_Appointment.csv to find confirmed appointments for the
		// given doctor
		try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
			String line;
			br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String appointmentDoctorID = data[2].trim();
				String status = data[5].trim();
				String outcome = data[6].trim();

				// Find appointments that are confirmed and not yet completed
				if (appointmentDoctorID.equals(doctorID) && status.equalsIgnoreCase("Confirmed")
						&& outcome.equals("-")) {
					upcomingAppointments.add(data);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + APPOINTMENT_FILE + ": " + e.getMessage());
			return;
		}

		if (upcomingAppointments.isEmpty()) {
			System.out.println("No upcoming appointments found for Doctor ID: " + doctorID);
			return;
		}

		// Step 2: Display the upcoming appointments
		System.out.println("=========================================");
		System.out.println("         Upcoming Appointment Slot       ");
		System.out.println("=========================================\n");

		for (String[] appointment : upcomingAppointments) {
			String appointmentID = appointment[0].trim();
			String patientID = appointment[1].trim();
			String date = appointment[3].trim();
			String timeSlot = appointment[4].trim();
			String patientName = patientNames.getOrDefault(patientID, "Unknown Patient");

			System.out.println("─────────────────────────────────────────");
			System.out.printf("         Appointment ID: %s%n", appointmentID);
			System.out.println("─────────────────────────────────────────");
			System.out.printf("%s [%s]%n", patientName, patientID);
			System.out.printf("Date : %s%n", date);
			System.out.printf("Time : %s%n", timeSlot);
			System.out.println();
		}

		// Step 3: Ask the doctor to select an appointment ID
		System.out.print("Select an Appointment ID: ");
		String selectedAppointmentID = scanner.nextLine().trim();

		// Find the selected appointment details
		String[] selectedAppointment = upcomingAppointments.stream().filter(app -> app[0].equals(selectedAppointmentID))
				.findFirst().orElse(null);

		if (selectedAppointment == null) {
			System.out.println("Invalid Appointment ID.");
			return;
		}

		// Display the selected appointment details
		String patientID = selectedAppointment[1].trim();
		String patientName = patientNames.getOrDefault(patientID, "Unknown Patient");
		String date = selectedAppointment[3].trim();
		String timeSlot = selectedAppointment[4].trim();

		System.out.println("─────────────────────────────────────────");
		System.out.printf("         Appointment ID: %s%n", selectedAppointmentID);
		System.out.println("─────────────────────────────────────────");
		System.out.printf("%s [%s]%n", patientName, patientID);
		System.out.printf("Date : %s%n", date);
		System.out.printf("Time : %s%n", timeSlot);
		System.out.println();

		System.out.print("Are you sure you want to edit this? (Yes / No): ");
		String confirmation = scanner.nextLine().trim().toLowerCase();
		System.out.print("\n");
		if (!confirmation.equals("yes")) {
			System.out.println("Operation cancelled.");
			return;
		}

		// Step 4: Gather details from the doctor
		System.out.print("Enter Diagnosis: ");
		String diagnosis = scanner.nextLine().trim();
		System.out.print("\n");
		if (!Pattern.matches("^[a-zA-Z\\s]+$", diagnosis)) {
			System.out.println("Invalid input for Diagnosis. Only text is allowed.");
			return;
		}

		System.out.print("Enter Treatment: ");
		String treatment = scanner.nextLine().trim();
		System.out.print("\n");
		if (!Pattern.matches("^[a-zA-Z\\s]+$", treatment)) {
			System.out.println("Invalid input for Treatment. Only text is allowed.");
			return;
		}

		// Load available medicines
		List<String> medicines = loadMedicines();
		if (medicines.isEmpty()) {
			System.out.println("No available medicines found.");
			return;
		}

		System.out.println("Available Medicines:");
		for (String medicine : medicines) {
			System.out.println("- " + medicine);
		}

		System.out.print("\nEnter Prescription (choose from the list): ");
		String prescription = scanner.nextLine().trim();
		System.out.print("\n");
		if (!medicines.contains(prescription)) {
			System.out.println("Invalid input for Prescription. Please select from the list.");
			return;
		}

		System.out.print("Enter Quantity: ");
		String quantityInput = scanner.nextLine().trim();
		System.out.print("\n");
		if (!Pattern.matches("^\\d+$", quantityInput)) {
			System.out.println("Invalid input for Quantity. Only numbers are allowed.");
			return;
		}
		int quantity = Integer.parseInt(quantityInput);

		System.out.print("Enter Case Notes: ");
		String caseNotes = scanner.nextLine().trim();
		System.out.print("\n");

		System.out.println("Available Type of Services:");
		System.out.println("- Consultation");
		System.out.println("- X-ray");
		System.out.println("- Blood Test");

		System.out.print("\nEnter Type of Service: ");
		String typeOfService = scanner.nextLine().trim();

		System.out.print("\n");
		
		// Check if the input matches any of the allowed options
		if (!typeOfService.equalsIgnoreCase("Consultation") &&
		    !typeOfService.equalsIgnoreCase("X-ray") &&
		    !typeOfService.equalsIgnoreCase("Blood Test")) {
		    System.out.println("Invalid input for Type of Service. Please select from the provided options.");
		    return;
		}

		appendDiagnosisToCSV(patientID, date, diagnosis, treatment, prescription, quantity, caseNotes, doctorID,
				selectedAppointmentID, typeOfService);

		// Step 6: Update the appointment outcome to "Completed"
		updateAppointmentOutcome(selectedAppointmentID, "Completed");
	}

	private static void updateAppointmentOutcome(String appointmentID, String newOutcome) {
		List<String[]> updatedAppointments = new ArrayList<>();
		boolean appointmentUpdated = false;

		try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(appointmentID)) {
					data[6] = newOutcome; // Update the outcome field to "Completed"
					appointmentUpdated = true;
				}
				updatedAppointments.add(data);
			}
		} catch (IOException e) {
			System.out.println("Error reading " + APPOINTMENT_FILE + ": " + e.getMessage());
			return;
		}

		// Write the updated data back to the file
		if (appointmentUpdated) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_FILE, false))) {
				for (String[] record : updatedAppointments) {
					writer.write(String.join(",", record));
					writer.newLine();
				}
				System.out.println("Appointment outcome updated to 'Completed'.\n");
			} catch (IOException e) {
				System.out.println("Error writing to " + APPOINTMENT_FILE + ": " + e.getMessage());
			}
		}
	}

	private static Map<String, String> loadPatientNames() {
		Map<String, String> patientNames = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Records.csv"))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length >= 2) {
					String patientID = data[0].trim();
					String patientName = data[1].trim();
					patientNames.put(patientID, patientName);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Records.csv: " + e.getMessage());
		}

		return patientNames;
	}

	private static List<String> loadMedicines() {
		List<String> medicines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
			String line;
			br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length >= 1) {
					String medicineName = data[0].trim();
					medicines.add(medicineName);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + MEDICINE_FILE + ": " + e.getMessage());
		}
		return medicines;
	}

	private static void appendDiagnosisToCSV(String patientID, String date, String diagnosis, String treatment,
			String prescription, int quantity, String caseNotes, String doctorID, String appointmentID,
			String typeOfService) {
		String status = "Pending";
		String[] diagnosisDetails = { patientID, date, diagnosis, treatment, prescription, String.valueOf(quantity),
				caseNotes, status, doctorID, appointmentID, typeOfService };

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(DIAGNOSIS_FILE, true))) {
			File diagnosisFile = new File(DIAGNOSIS_FILE);

			// Check if the file is not empty and if the last line has a trailing newline
			if (diagnosisFile.length() > 0) {
				try (RandomAccessFile fileHandler = new RandomAccessFile(diagnosisFile, "r")) {
					fileHandler.seek(diagnosisFile.length() - 1);
					int lastChar = fileHandler.read();
					if (lastChar != '\n') {
						writer.newLine(); // Add a newline if not present
					}
				}
			}

			// Append the new diagnosis details
			writer.append(String.join(",", diagnosisDetails));
			writer.newLine(); // Ensure the new entry is in a new row
			System.out.println("Diagnosis details recorded successfully.\n");
		} catch (IOException e) {
			System.out.println("Error writing to " + DIAGNOSIS_FILE + ": " + e.getMessage());
		}
	}
	
	public static void viewAllAppointmentsAndOutcomesForAdmin(Scanner scanner) {
	    List<String[]> upcomingAppointments = new ArrayList<>();
	    List<String[]> cancelledAppointments = new ArrayList<>();
	    List<String[]> completedAppointments = new ArrayList<>();
	    Map<String, Map<String, String[]>> diagnosisDetailsByPatient = new HashMap<>(); // Store diagnosis by patient ID

	    // Load patient names for displaying in the appointment list
	    Map<String, String> patientNames = loadPatientNames();

	    // Load all diagnosis details from all patients
	    try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            String patientID = data[0].trim();
	            diagnosisDetailsByPatient.putIfAbsent(patientID, new HashMap<>());
	            diagnosisDetailsByPatient.get(patientID).put(data[9].trim(), // Appointment ID
	                    new String[]{data[2].trim(), data[3].trim(), data[4].trim(), data[5].trim(), data[6].trim(), data[10].trim()});
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Read appointments from Patient_Appointment.csv for all patients
	    try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
	        String line = br.readLine(); // Skip header
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            String appointmentID = data[0].trim();
	            String patientID = data[1].trim(); // Extract patient ID from the CSV
	            String doctorID = data[2].trim();
	            String date = data[3].trim();
	            String timeSlot = data[4].trim();
	            String status = data[5].trim();
	            String outcome = data[6].trim();

	            if ((status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Pending")) && outcome.equals("-")) {
	                upcomingAppointments.add(new String[]{appointmentID, patientID, doctorID, date, timeSlot, status});
	            } else if (status.contains("Cancelled")) {
	                cancelledAppointments.add(new String[]{appointmentID, patientID, doctorID, date, timeSlot, status});
	            } else if (outcome.equalsIgnoreCase("Completed")) {
	                completedAppointments.add(new String[]{appointmentID, patientID, doctorID, date, timeSlot, status});
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Display all Upcoming Appointments
	    System.out.println("=========================================");
	    System.out.println("         Upcoming Appointments");
	    System.out.println("=========================================");
	    displayAppointmentListForAdmin(upcomingAppointments, patientNames);

	    // Display all Cancelled Appointments
	    System.out.println("=========================================");
	    System.out.println("         Cancelled Appointments");
	    System.out.println("=========================================");
	    displayAppointmentListForAdmin(cancelledAppointments, patientNames);

	    // Display all Completed Appointments with Outcomes
	    System.out.println("=========================================");
	    System.out.println("         Completed Appointments");
	    System.out.println("=========================================");
	    displayCompletedAppointmentsForAdmin(completedAppointments, patientNames, diagnosisDetailsByPatient);
	}

	private static void displayAppointmentListForAdmin(List<String[]> appointments, Map<String, String> patientNames) {
	    for (String[] appointment : appointments) {
	        String appointmentID = appointment[0];
	        String patientID = appointment[1];
	        String doctorID = appointment[2];
	        String appointmentDate = appointment[3];
	        String timeSlot = appointment[4];
	        String status = appointment[5];
	        String doctorName = loadDoctorName(doctorID); // Get doctor name by ID
	        String patientName = patientNames.getOrDefault(patientID, "Unknown Patient");

	        System.out.printf("Appointment ID   : %s%n", appointmentID);
	        System.out.printf("Patient          : %s [%s]%n", patientName, patientID);
	        System.out.printf("Doctor           : %s [%s]%n", doctorName, doctorID);
	        System.out.printf("Date             : %s%n", appointmentDate);
	        System.out.printf("Time Slot        : %s%n", timeSlot);
	        System.out.printf("Status           : %s%n", status + "\n");
	    }
	}

	private static void displayCompletedAppointmentsForAdmin(List<String[]> appointments, Map<String, String> patientNames, Map<String, Map<String, String[]>> diagnosisDetailsByPatient) {
	    for (String[] appointment : appointments) {
	        String appointmentID = appointment[0];
	        String patientID = appointment[1];
	        String doctorID = appointment[2];
	        String appointmentDate = appointment[3];
	        String timeSlot = appointment[4];
	        String status = appointment[5];
	        String doctorName = loadDoctorName(doctorID);
	        String patientName = patientNames.getOrDefault(patientID, "Unknown Patient");

	        System.out.printf("Appointment ID   : %s%n", appointmentID);
	        System.out.printf("Patient          : %s [%s]%n", patientName, patientID);
	        System.out.printf("Doctor           : %s [%s]%n", doctorName, doctorID);
	        System.out.printf("Date             : %s%n", appointmentDate);
	        System.out.printf("Time Slot        : %s%n", timeSlot);
	        System.out.printf("Status           : %s%n", status + "\n");

	        Map<String, String[]> diagnosisMap = diagnosisDetailsByPatient.getOrDefault(patientID, new HashMap<>());
	        if (diagnosisMap.containsKey(appointmentID)) {
	            String[] diagnosisDetails = diagnosisMap.get(appointmentID);
	            System.out.printf("Diagnosis        : %s%n", diagnosisDetails[0]);
	            System.out.printf("Treatment        : %s%n", diagnosisDetails[1]);
	            System.out.printf("Prescription     : %s%n", diagnosisDetails[2]);
	            System.out.printf("Quantity         : %s%n", diagnosisDetails[3]);
	            System.out.printf("Case Notes       : %s%n", diagnosisDetails[4]);
	            System.out.printf("Type of Service  : %s%n", diagnosisDetails[5] + "\n");
				String separator = "─────────────────────────────────────────";
				System.out.println(separator + "\n");
	            
	        } else {
	            System.out.println("No diagnosis details found.");
	        }
	    }
	}

}
