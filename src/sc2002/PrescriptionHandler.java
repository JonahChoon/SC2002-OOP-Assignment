package sc2002;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrescriptionHandler {

	private static final String MEDICINE_REQUEST_FILE = "data/Medicine_Request.csv";

	public static void viewPendingPrescriptions() {
		List<String[]> pendingPrescriptions = new ArrayList<>();

		// Read from Patient_Diagnosis_Prescription.csv to find pending or dispensed
		// prescriptions
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line;
			br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String status = data[7].trim(); // Status is in the 8th column (index 7)

				// Include records with both "Pending" and "Dispensed" statuses
				if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Dispensed")) {
					pendingPrescriptions.add(data);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Diagnosis_Prescription.csv: " + e.getMessage());
			return;
		}

		// Display pending or dispensed prescriptions if any are found
		if (pendingPrescriptions.isEmpty()) {
			System.out.println("No pending or dispensed prescriptions found.");
			return;
		}

		System.out.println("=========================================");
		System.out.println("         Completed Appointments          ");
		System.out.println("=========================================");

		// Display each prescription's details
		for (String[] prescription : pendingPrescriptions) {
			String appointmentID = prescription[9].trim(); // Appointment ID
			String patientID = prescription[0].trim(); // Patient ID
			String date = prescription[1].trim();
			String diagnosis = prescription[2].trim(); // Diagnosis
			String treatment = prescription[3].trim(); // Treatment
			String prescriptionName = prescription[4].trim(); // Prescription name
			String quantity = prescription[5].trim(); // Quantity
			String caseNotes = prescription[6].trim(); // Case Notes
			String status = prescription[7].trim(); // Status
			String staffID = prescription[8].trim(); // Doctor ID
			String typeOfService = prescription[10].trim(); // Type of Service

			String patientName = loadPatientName(patientID);
			String doctorName = loadDoctorName(staffID);

			System.out.println("─────────────────────────────────────────");
			System.out.printf("Appointment ID   : %s%n", appointmentID);
			System.out.println("─────────────────────────────────────────");
			System.out.printf("Patient          : %s [%s]%n", patientName, patientID);
			System.out.printf("Doctor           : %s [%s]%n", doctorName, staffID);
			System.out.printf("Date             : %s%n", date);
			System.out.printf("Type of Service  : %s%n", typeOfService);
			System.out.printf("Diagnosis        : %s%n", diagnosis);
			System.out.printf("Treatment        : %s%n", treatment);
			System.out.printf("Prescription     : %s%n", prescriptionName);
			System.out.printf("Quantity         : %s%n", quantity);
			System.out.printf("Case Notes       : %s%n", caseNotes);
			System.out.printf("Status           : %s%n", status);
			System.out.println();
		}
	}

	// Helper function to load patient names from Patient_Records.csv
	private static String loadPatientName(String patientID) {
		String patientName = "Unknown Patient";
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Records.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(patientID)) {
					patientName = data[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Records.csv: " + e.getMessage());
		}
		return patientName;
	}

	// Helper function to load doctor names from Staff_Records.csv
	private static String loadDoctorName(String staffID) {
		String doctorName = "Unknown Doctor";
		try (BufferedReader br = new BufferedReader(new FileReader("data/Staff_Records.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(staffID)) {
					doctorName = data[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Staff_Records.csv: " + e.getMessage());
		}
		return doctorName;
	}

	public static void updatePrescriptionStatus(Scanner scanner) {
		List<String[]> pendingPrescriptions = new ArrayList<>();

		// Read from Patient_Diagnosis_Prescription.csv to find only pending
		// prescriptions
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line;
			br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String status = data[7].trim(); // Status is in the 8th column (index 7)

				// Include only "Pending" records
				if (status.equalsIgnoreCase("Pending")) {
					pendingPrescriptions.add(data);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Diagnosis_Prescription.csv: " + e.getMessage());
			return;
		}

		// Display pending prescriptions if any are found
		if (pendingPrescriptions.isEmpty()) {
			System.out.println("No pending prescriptions found.");
			return;
		}

		System.out.println("=========================================");
		System.out.println("         Pending Prescriptions           ");
		System.out.println("=========================================");

		// Display each pending prescription's details
		for (String[] prescription : pendingPrescriptions) {
			String appointmentID = prescription[9].trim(); // Appointment ID
			String patientID = prescription[0].trim();// Patient ID
			String diagnosis = prescription[2].trim(); // Diagnosis
			String treatment = prescription[3].trim(); // Treatment
			String prescriptionName = prescription[4].trim(); // Prescription name
			String quantity = prescription[5].trim(); // Quantity
			String caseNotes = prescription[6].trim(); // Case Notes
			String staffID = prescription[8].trim(); // Doctor ID
			String typeOfService = prescription[10].trim(); // Type of Service

			String patientName = loadPatientName(patientID);
			String doctorName = loadDoctorName(staffID);

			System.out.println("─────────────────────────────────────────");
			System.out.printf("Appointment ID   : %s%n", appointmentID);
			System.out.println("─────────────────────────────────────────");
			System.out.printf("Patient          : %s [%s]%n", patientName, patientID);
			System.out.printf("Doctor           : %s [%s]%n", doctorName, staffID);
			System.out.printf("Type of Service  : %s%n", typeOfService);
			System.out.printf("Diagnosis        : %s%n", diagnosis);
			System.out.printf("Treatment        : %s%n", treatment);
			System.out.printf("Prescription     : %s%n", prescriptionName);
			System.out.printf("Quantity         : %s%n", quantity);
			System.out.printf("Case Notes       : %s%n", caseNotes);
			System.out.println();
		}

		// Prompt to select an Appointment ID
		System.out.print("Enter the Appointment ID to dispense: ");
		String selectedAppointmentID = scanner.nextLine().trim();
		System.out.print("\n");
		// Find the selected prescription
		String[] selectedPrescription = pendingPrescriptions.stream()
				.filter(prescription -> prescription[9].trim().equals(selectedAppointmentID)).findFirst().orElse(null);

		if (selectedPrescription == null) {
			System.out.println("Invalid Appointment ID selected.");
			return;
		}

		// Confirm with the user if they want to dispense the medication
		System.out.print("Do you want to dispense this medication? (Yes/No): ");
		String confirmation = scanner.nextLine().trim().toLowerCase();
		System.out.print("\n");

		if (confirmation.equals("yes")) {
			// Proceed to update the stock
			String prescriptionName = selectedPrescription[4].trim();
			int quantityRequested = Integer.parseInt(selectedPrescription[5].trim());

			// Update the stock in Medicine_Stock.csv
			if (updateMedicineStock(prescriptionName, quantityRequested)) {
				// Update the status in Patient_Diagnosis_Prescription.csv to "Dispensed"
				updatePrescriptionStatusInCSV(selectedAppointmentID, "Dispensed");
				System.out.println("Medication dispensed successfully.\n");
			} else {
				System.out.println("Failed to dispense medication. Insufficient stock.");
			}
		} else {
			System.out.println("Dispensing cancelled.");
		}
	}
	

	// Helper method to update the stock in Medicine_Stock.csv
	private static boolean updateMedicineStock(String prescriptionName, int quantityRequested) {
		List<String[]> updatedStock = new ArrayList<>();
		boolean stockUpdated = false;

		try (BufferedReader br = new BufferedReader(new FileReader("data/Medicine_Stock.csv"))) {
			String line = br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String medicineName = data[0].trim();
				int currentStock = Integer.parseInt(data[1].trim()); // Parse the stock quantity

				if (medicineName.equalsIgnoreCase(prescriptionName)) {
					if (currentStock >= quantityRequested) {
						int newStock = currentStock - quantityRequested;
						data[1] = String.valueOf(newStock);
						stockUpdated = true;
					} else {
						System.out.println(
								"Not enough stock for " + prescriptionName + ". Current stock: " + currentStock);
						return false;
					}
				}

				updatedStock.add(data);
			}
		} catch (IOException e) {
			System.out.println("Error reading Medicine_Stock.csv: " + e.getMessage());
			return false;
		} catch (NumberFormatException e) {
			System.out.println("Invalid stock value in Medicine_Stock.csv: " + e.getMessage());
			return false;
		}

		// Write the updated stock back to the CSV file
		if (stockUpdated) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Medicine_Stock.csv"))) {
				// Write the header back (assuming it's known)
				writer.write("Medicine,Current Stock");
				writer.newLine();
				for (String[] record : updatedStock) {
					writer.write(String.join(",", record));
					writer.newLine();
				}
			} catch (IOException e) {
				System.out.println("Error updating Medicine_Stock.csv: " + e.getMessage());
				return false;
			}
		}

		return stockUpdated;
	}

	// Helper method to update the status in Patient_Diagnosis_Prescription.csv
	private static void updatePrescriptionStatusInCSV(String appointmentID, String newStatus) {
		List<String[]> updatedPrescriptions = new ArrayList<>();
		boolean prescriptionUpdated = false;

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[9].trim().equals(appointmentID)) {
					data[7] = newStatus; // Update the status to "Dispensed"
					prescriptionUpdated = true;
				}
				updatedPrescriptions.add(data);
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Diagnosis_Prescription.csv: " + e.getMessage());
			return;
		}

		// Write the updated prescriptions back to the CSV file
		if (prescriptionUpdated) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Diagnosis_Prescription.csv"))) {
				for (String[] record : updatedPrescriptions) {
					writer.write(String.join(",", record));
					writer.newLine();
				}
			} catch (IOException e) {
				System.out.println("Error updating Patient_Diagnosis_Prescription.csv: " + e.getMessage());
			}
		}
	}

	static void viewLowStockWarnings() {
		String medicineStockFilePath = "data/Medicine_Stock.csv";
		boolean hasWarnings = false;

		// Step 1: Read from the Medicine_Stock.csv file
		try (BufferedReader br = new BufferedReader(new FileReader(medicineStockFilePath))) {
			String line;
			boolean isFirstLine = true; // Skip the header line

			while ((line = br.readLine()) != null) {
				if (isFirstLine) {
					isFirstLine = false; // Skip the first line, which is the header
					continue;
				}

				String[] values = line.split(",");
				String medicineName = values[0]; // Column A: Medicine Name
				int currentStock = Integer.parseInt(values[1]); // Column B: Current Stock
				int lowStockAlert = Integer.parseInt(values[2]); // Column C: Low Stock Level Alert

				// Step 2: Check if the stock is low and display a warning message
				if (currentStock <= lowStockAlert) {
					if (!hasWarnings) {
						System.out.println("\n[!] WARNING: Low Stock Medications:");
					}
					System.out.println("[!] " + medicineName + " is currently running low, please refill!");
					hasWarnings = true;
				}
			}

			if (!hasWarnings) {
				System.out.println("No medications are running low at the moment.\n");
			}

		} catch (IOException e) {
			System.out.println("Error reading Medicine_Stock.csv: " + e.getMessage());
		}
	}

	public static void viewMedicineStock() {

		viewLowStockWarnings();

		List<String[]> medicineStock = new ArrayList<>();

		// Read from Medicine_Stock.csv to get the stock information
		try (BufferedReader br = new BufferedReader(new FileReader("data/Medicine_Stock.csv"))) {
			String line = br.readLine(); // Read the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				medicineStock.add(data);
			}
		} catch (IOException e) {
			System.out.println("Error reading Medicine_Stock.csv: " + e.getMessage());
			return;
		}

		// Display the medicine stock if any are found
		if (medicineStock.isEmpty()) {
			System.out.println("No medicines found in stock.");
			return;
		}

		System.out.println("=========================================");
		System.out.println("            Medicine Stock               ");
		System.out.println("=========================================");
		System.out.printf("%-20s %-10s%n", "Medicine", "Current Stock");
		System.out.println("─────────────────────────────────────────");

		// Display each medicine's details
		for (String[] medicine : medicineStock) {
			String medicineName = medicine[0].trim();
			String currentStock = medicine[1].trim();
			System.out.printf("%-20s %-10s%n", medicineName, currentStock);
		}
	}

	public static void submitReplenishmentRequest(Scanner scanner, String staffId) {

		viewMedicineStock();
		// Step 1: Get a list of available medicines from the stock file.
		List<String> availableMedicines = getAvailableMedicines();

		// Step 2: Display available medicines to the user.
		if (availableMedicines.isEmpty()) {
			System.out.println("No medicines available in stock for replenishment.");
			return;
		}

		System.out.println("\nAvailable Medicines:");
		for (String medicine : availableMedicines) {
			System.out.println("- " + medicine);
		}
		System.out.println();

		// Step 3: Prompt for the medicine name.
		System.out.print("Enter the Medicine Name: ");
		String medicineName = scanner.nextLine().trim();

		// Step 4: Validate the input against the available medicines.
		if (!availableMedicines.contains(medicineName)) {
			System.out.println("Invalid medicine name. Please select from the available medicines listed above.");
			return;
		}

		// Step 5: Prompt for the quantity to request.
		System.out.print("Enter the quantity to request: ");
		int requestedQuantity;
		try {
			requestedQuantity = Integer.parseInt(scanner.nextLine().trim());
			if (requestedQuantity <= 0) {
				System.out.println("Quantity must be greater than zero.");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input for quantity. Please enter a number.");
			return;
		}

		// Step 6: Call the method to write the request to the file with the staffId.
		writeReplenishmentRequest(medicineName, requestedQuantity, staffId);
	}

	public static void writeReplenishmentRequest(String medicineName, int requestedQuantity, String staffId) {
		String filePath = "data/Medicine_Request.csv";
		String status = "Pending";
		String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
			File file = new File(filePath);
			// Check if the file is not empty and if the last line has a trailing newline
			if (file.length() > 0) {
				try (RandomAccessFile fileHandler = new RandomAccessFile(file, "r")) {
					fileHandler.seek(file.length() - 1);
					int lastChar = fileHandler.read();
					if (lastChar != '\n') {
						bw.newLine(); // Add a newline if not present
					}
				}
			}

			// Append the new request in the specified format
			bw.write(medicineName + "," + requestedQuantity + "," + todayDate + "," + staffId + "," + status + ","
					+ "-");
			bw.newLine();
			System.out.println("Replenishment request for " + medicineName + " has been recorded successfully.");
		} catch (IOException e) {
			System.out.println("Error writing to Medicine_Request.csv: " + e.getMessage());
		}
	}

	private static List<String> getAvailableMedicines() {
		List<String> availableMedicines = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("data/Medicine_Stock.csv"))) {
			String line;
			boolean isFirstLine = true; // To skip the header line if present

			while ((line = br.readLine()) != null) {
				// Skip the header line
				if (isFirstLine) {
					isFirstLine = false;
					continue;
				}

				String[] data = line.split(",");
				if (data.length >= 1) {
					String medicineName = data[0].trim();
					availableMedicines.add(medicineName);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Medicine_Stock.csv: " + e.getMessage());
		}

		return availableMedicines;
	}

	public static void viewAllMedicineRequests() {
		String filePath = "data/Medicine_Request.csv";
		Map<String, List<String[]>> medicineRequests = new HashMap<>(); // To store requests grouped by medicine name

		// Read the Medicine_Request.csv file and group requests by medicine name
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = br.readLine(); // Skip header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				// Validate that we have at least 6 columns before proceeding
				if (data.length < 6) {
					System.out.println("Skipping malformed line: " + line);
					continue; // Skip any malformed line
				}

				String medicineName = data[0].trim();
				String requestedQuantity = data[1].trim();
				String requestDate = data[2].trim();
				String staffID = data[3].trim();
				String status = data[4].trim();
				String approvedBy = data[5].trim();

				// Format the request details as a string array
				String[] requestDetails = { requestDate, requestedQuantity, staffID, status, approvedBy };

				// Add the request details to the appropriate medicine group
				medicineRequests.putIfAbsent(medicineName, new ArrayList<>());
				medicineRequests.get(medicineName).add(requestDetails);
			}
		} catch (IOException e) {
			System.out.println("Error reading " + filePath + ": " + e.getMessage());
			return;
		}

		// Display each medicine's requests, sorted by request date
		SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/yy");
		for (Map.Entry<String, List<String[]>> entry : medicineRequests.entrySet()) {
			String medicineName = entry.getKey();
			List<String[]> requests = entry.getValue();

			// Sort requests by date
			requests.sort((a, b) -> {
				try {
					Date date1 = dateFormatter.parse(a[0]);
					Date date2 = dateFormatter.parse(b[0]);
					return date1.compareTo(date2);
				} catch (ParseException e) {
					e.printStackTrace();
					return 0;
				}
			});

			// Display formatted output
			System.out.println("─────────────────────────────────────────");
			System.out.println(medicineName);
			System.out.println("─────────────────────────────────────────");
			for (String[] request : requests) {
				System.out.printf("Request Date      : %s%n", request[0]);
				System.out.printf("Request Quantity  : %s%n", request[1]);
				System.out.printf("Request Staff ID  : %s%n", request[2]);
				System.out.printf("Status            : %s%n", request[3]);
				System.out.printf("Approved By       : %s%n",
						request[3].equalsIgnoreCase("Pending") ? "-" : request[4]);
				System.out.println();
			}
		}
	}

	
	public static void approveReplenishmentRequests(Scanner scanner, String adminID) {
	    List<String[]> pendingRequests = new ArrayList<>();

	    // Load pending requests from Medicine_Request.csv
	    try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_REQUEST_FILE))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            if (data.length >= 6 && data[4].trim().equalsIgnoreCase("Pending")) { // Check if status is "Pending"
	                pendingRequests.add(data);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading Medicine_Request.csv: " + e.getMessage());
	        return;
	    }

	    // Display pending requests
	    if (pendingRequests.isEmpty()) {
	        System.out.println("No pending requests found.");
	        return;
	    }

	    System.out.println("=========================================");
	    System.out.println("      Pending Replenishment Requests     ");
	    System.out.println("=========================================");

	    for (int i = 0; i < pendingRequests.size(); i++) {
	        String[] request = pendingRequests.get(i);
	        System.out.printf("%d. %s%n", i + 1, request[0].trim()); // Medicine Name
	        System.out.println("Request Date      : " + request[2].trim());
	        System.out.println("Request Quantity  : " + request[1].trim());
	        System.out.println("Request Staff ID  : " + request[3].trim());
	        System.out.println("\n");

	    }

	    // Prompt admin to select a request to approve
	    System.out.print("Enter the number of the request to approve: ");
	    int selection;
	    try {
	        selection = Integer.parseInt(scanner.nextLine().trim());
	        if (selection < 1 || selection > pendingRequests.size()) {
	            System.out.println("Invalid selection. Operation cancelled.");
	            return;
	        }
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid input. Operation cancelled.");
	        return;
	    }

	    // Update the selected request in Medicine_Request.csv
	    String[] selectedRequest = pendingRequests.get(selection - 1);
	    String medicineName = selectedRequest[0].trim();
	    int quantityToAdd = Integer.parseInt(selectedRequest[1].trim());
	    selectedRequest[4] = "Approved"; // Update status to "Approved"
	    selectedRequest[5] = adminID; // Set the "Approved By" to admin ID

	    // Rewrite the Medicine_Request.csv file with updated data
	    try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_REQUEST_FILE));
	            BufferedWriter writer = new BufferedWriter(new FileWriter("data/Medicine_Request_temp.csv"))) {

	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            if (data.length >= 6 && data[0].trim().equals(medicineName)
	                    && data[2].trim().equals(selectedRequest[2].trim()) && data[4].trim().equals("Pending")) {
	                writer.write(String.join(",", selectedRequest)); // Write updated line
	            } else {
	                writer.write(line); // Write unmodified lines
	            }
	            writer.newLine();
	        }

	    } catch (IOException e) {
	        System.out.println("Error updating Medicine_Request.csv: " + e.getMessage());
	        return;
	    }

	    // Rename temp file to original Medicine_Request.csv
	    new File("data/Medicine_Request.csv").delete();
	    new File("data/Medicine_Request_temp.csv").renameTo(new File("data/Medicine_Request.csv"));

	    // Update the Medicine Stock
	    addMedicineStock(medicineName, quantityToAdd); // Call modified function for adding stock
	}

	// Updated method to add stock instead of reducing it
	private static void addMedicineStock(String medicineName, int quantityToAdd) {
	    List<String[]> updatedStock = new ArrayList<>();
	    boolean stockUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader("data/Medicine_Stock.csv"))) {
	        String line = br.readLine(); // Skip the header
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            String currentMedicineName = data[0].trim();
	            int currentStock = Integer.parseInt(data[1].trim()); // Parse the stock quantity

	            if (currentMedicineName.equalsIgnoreCase(medicineName)) {	                
	                int newStock = currentStock + quantityToAdd; // Add the requested quantity
	                data[1] = String.valueOf(newStock);
	                stockUpdated = true;
	                System.out.printf("Updated stock for %s. New stock: %d%n", medicineName, newStock);
	            }

	            updatedStock.add(data);
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading Medicine_Stock.csv: " + e.getMessage());
	        return;
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid stock value in Medicine_Stock.csv: " + e.getMessage());
	        return;
	    }

	    // Write the updated stock back to the CSV file
	    if (stockUpdated) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Medicine_Stock.csv"))) {
	            writer.write("Medicine,Current Stock"); // Rewriting the header
	            writer.newLine();
	            for (String[] record : updatedStock) {
	                writer.write(String.join(",", record));
	                writer.newLine();
	            }
	            System.out.println("\n");
	        } catch (IOException e) {
	            System.out.println("Error updating Medicine_Stock.csv: " + e.getMessage());
	        }
	    }
	}

}
