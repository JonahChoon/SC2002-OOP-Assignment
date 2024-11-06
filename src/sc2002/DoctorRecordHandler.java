package sc2002;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class DoctorRecordHandler {

	public static void viewAllPatientsUnderCare(String doctorID) {
		Set<String> patientIDs = new HashSet<>();
		List<Patient> patientsUnderCare = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length >= 9) {
					String recordedDoctorID = data[8].trim();
					if (recordedDoctorID.equals(doctorID)) {
						patientIDs.add(data[0].trim());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Diagnosis_Prescription.csv: " + e.getMessage());
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Records.csv"))) {
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length >= 7 && patientIDs.contains(data[0].trim())) {
					Patient patient = new Patient(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(),
							data[4].trim(), data[5].trim(), data[6].trim());
					patientsUnderCare.add(patient);
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Records.csv: " + e.getMessage());
			return;
		}

		Map<String, String> doctorNames = Doctor.loadDoctorNamesFromCSV("data/Staff_Records.csv");

		if (patientsUnderCare.isEmpty()) {
			System.out.println("No patients found under the care of Doctor ID: " + doctorID);
		} else {
			for (Patient patient : patientsUnderCare) {
				MedicalRecordHandler.displayPatientDetails(patient);

				List<MedicalRecordHandler.MedicalHistory> history = MedicalRecordHandler
						.getPatientMedicalHistory(patient.getPatientID());

				for (MedicalRecordHandler.MedicalHistory record : history) {
					String doctorName = doctorNames.getOrDefault(record.getDoctorID(), "Unknown Doctor");

					System.out.printf("Date                : %s%n", record.getDate());
					System.out.printf("Doctor Name [ID]    : %s [%s]%n", doctorName, record.getDoctorID());
					System.out.printf("Appointment ID      : %s%n", record.getAppointmentID());
					System.out.printf("Type of Service     : %s%n", record.getTypeOfService());
					System.out.printf("Diagnosis           : %s%n", record.getPastDiagnosis());
					System.out.printf("Treatment           : %s%n", record.getTreatment());
					System.out.printf("Case Notes          : %s%n", record.getCaseNotes());
					System.out.println();
				}
			}
		}
	}

	public static void updatePatientMedicalRecord(String doctorID, Scanner scanner) {
	    viewAllPatientsUnderCare(doctorID);

	    System.out.print("Please insert the Patient ID to update the medical information: ");
	    String patientID = scanner.nextLine().trim();

	    if (!Pattern.matches("^P\\d{4}$", patientID)) {
	        System.out.println("Invalid Patient ID format. Please use PXXXX where X is a digit.");
	        return;
	    }

	    List<MedicalRecordHandler.MedicalHistory> history = MedicalRecordHandler.getPatientMedicalHistory(patientID);

	    if (history.isEmpty()) {
	        System.out.println("No medical records found for Patient ID: " + patientID);
	        return;
	    }

	    for (MedicalRecordHandler.MedicalHistory record : history) {
	        System.out.printf("Date                : %s%n", record.getDate());
	        System.out.printf("Doctor Name [ID]    : %s [%s]%n", record.getDoctorID(), doctorID);
	        System.out.printf("Appointment ID      : %s%n", record.getAppointmentID());
	        System.out.printf("Type of Service     : %s%n", record.getTypeOfService());
	        System.out.printf("Diagnosis           : %s%n", record.getPastDiagnosis());
	        System.out.printf("Treatment           : %s%n", record.getTreatment());
	        System.out.printf("Case Notes          : %s%n", record.getCaseNotes());
	        System.out.println();
	    }

	    System.out.print("Enter Appointment ID to update: ");
	    String appointmentID = scanner.nextLine().trim();

	    if (!Pattern.matches("^AP\\d{3}$", appointmentID)) {
	        System.out.println("Invalid Appointment ID format. Please use APXXX where X is a digit.");
	        return;
	    }

	    MedicalRecordHandler.MedicalHistory recordToUpdate = history.stream()
	            .filter(record -> record.getAppointmentID().equals(appointmentID)).findFirst().orElse(null);

	    if (recordToUpdate == null) {
	        System.out.println("No matching appointment found.");
	        return;
	    }

	    System.out.println("1. Update Type of Service");
	    System.out.println("2. Update Diagnosis");
	    System.out.println("3. Update Treatment");
	    System.out.println("4. Update Case Notes");
	    System.out.print("\nChoose an option: ");
	    String option = scanner.nextLine().trim();

	    switch (option) {
	        case "1":
	            System.out.println("Available Type of Services:");
	            System.out.println("- Consultation");
	            System.out.println("- X-ray");
	            System.out.println("- Blood Test");
	            System.out.print("Enter Type of Service: ");
	            String newService = scanner.nextLine().trim();

	            if (!Pattern.matches("^(Consultation|X-ray|Blood Test)$", newService)) {
	                System.out.println("Invalid Type of Service. Please choose from the available options.");
	                return;
	            }

	            updateMedicalRecordInCSV(patientID, appointmentID, newService, null, null, null);
	            break;

	        case "2":
	            System.out.print("Enter new Diagnosis: ");
	            String newDiagnosis = scanner.nextLine().trim();

	            if (!Pattern.matches("^[a-zA-Z\\s]+$", newDiagnosis)) {
	                System.out.println("Invalid input for Diagnosis. Only letters and spaces are allowed.");
	                return;
	            }

	            updateMedicalRecordInCSV(patientID, appointmentID, null, newDiagnosis, null, null);
	            break;

	        case "3":
	            System.out.print("Enter new Treatment: ");
	            String newTreatment = scanner.nextLine().trim();

	            if (!Pattern.matches("^[a-zA-Z\\s]+$", newTreatment)) {
	                System.out.println("Invalid input for Treatment. Only letters and spaces are allowed.");
	                return;
	            }

	            updateMedicalRecordInCSV(patientID, appointmentID, null, null, newTreatment, null);
	            break;

	        case "4":
	            System.out.print("Enter new Case Notes: ");
	            String newCaseNotes = scanner.nextLine().trim();
	            updateMedicalRecordInCSV(patientID, appointmentID, null, null, null, newCaseNotes);
	            break;

	        default:
	            System.out.println("Invalid option.");
	    }
	}

	private static void updateMedicalRecordInCSV(String patientID, String appointmentID, String newService,
			String newDiagnosis, String newTreatment, String newCaseNotes) {
		List<String[]> updatedRecords = new ArrayList<>();
		boolean recordUpdated = false;

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data[0].trim().equals(patientID) && data[9].trim().equals(appointmentID)) {
					if (newService != null)
						data[10] = newService;
					if (newDiagnosis != null)
						data[2] = newDiagnosis;
					if (newTreatment != null)
						data[3] = newTreatment;
					if (newCaseNotes != null)
						data[6] = newCaseNotes;
					recordUpdated = true;
				}
				updatedRecords.add(data);
			}
		} catch (IOException e) {
			System.out.println("Error reading Patient_Diagnosis_Prescription.csv: " + e.getMessage());
		}

		if (recordUpdated) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Diagnosis_Prescription.csv"))) {
				for (String[] record : updatedRecords) {
					writer.write(String.join(",", record));
					writer.newLine();
				}
				System.out.println("Medical record updated successfully." + "\n");
			} catch (IOException e) {
				System.out.println("Error updating medical record: " + e.getMessage() + "\n");
			}
		} else {
			System.out.println("No matching record found to update.");
		}
	}

	public static void viewDoctorAvailability(String doctorID) {
		Map<LocalDate, List<String>> availabilityMap = new HashMap<>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

		try (BufferedReader br = new BufferedReader(new FileReader("data/Doctor_Availability.csv"))) {
			String line;
			br.readLine(); // Skip the header
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				String recordedDoctorID = data[0].trim();
				String date = data[1].trim();
				String timeSlot = data[2].trim();
				String status = data[3].trim();

				if (recordedDoctorID.equals(doctorID) && status.equalsIgnoreCase("Available")) {
					try {
						LocalDate parsedDate = LocalDate.parse(date, dateFormatter);
						availabilityMap.putIfAbsent(parsedDate, new ArrayList<>());
						availabilityMap.get(parsedDate).add(timeSlot);
					} catch (DateTimeParseException e) {
						System.out.println("Invalid date format found: " + date);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading Doctor_Availability.csv: " + e.getMessage());
			return;
		}

		if (availabilityMap.isEmpty()) {
			System.out.println("No available time slots found for Doctor ID: " + doctorID);
		} else {
			String doctorName = Doctor.loadDoctorName(doctorID);
			String separator = "=========================================";
			System.out.println(separator);
			System.out.println(doctorName + " [" + doctorID + "] Availability");
			System.out.println(separator);

			List<LocalDate> sortedDates = new ArrayList<>(availabilityMap.keySet());
			Collections.sort(sortedDates);

			for (LocalDate date : sortedDates) {
				System.out.println("Date: " + date.format(dateFormatter));
				List<String> timeSlots = availabilityMap.get(date);
				for (String timeSlot : timeSlots) {
					System.out.println(timeSlot);
				}
				System.out.println();
			}
		}
	}

	public static void setDoctorAvailability(String doctorID, Scanner scanner) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

		System.out.print("Enter the date of availability (dd/MM/yy): ");
		String dateInput = scanner.nextLine().trim();

		LocalDate date;
		try {
			date = LocalDate.parse(dateInput, dateFormatter);
		} catch (DateTimeParseException e) {
			System.out.println("Invalid date format. Please use dd/MM/yy.");
			return;
		}

		System.out.print("Enter the time range (e.g., 1100 - 1800): ");
		String timeRangeInput = scanner.nextLine().trim();

		String timeRangePattern = "^(\\d{4})\\s*-\\s*(\\d{4})$";
		if (!Pattern.matches(timeRangePattern, timeRangeInput)) {
			System.out.println("Invalid time range format. Please use the format: 1100 - 1800.");
			return;
		}

		String[] timeRangeParts = timeRangeInput.split("-");
		String startTimeStr = timeRangeParts[0].trim();
		String endTimeStr = timeRangeParts[1].trim();

		LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HHmm"));
		LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HHmm"));

		if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
			System.out.println("End time must be after the start time.");
			return;
		}

		List<String> timeSlots = new ArrayList<>();

		LocalTime currentTime = startTime;
		while (currentTime.isBefore(endTime)) {
			LocalTime nextTime = currentTime.plusHours(1);
			String timeSlot = currentTime.format(DateTimeFormatter.ofPattern("HHmm")) + " - "
					+ nextTime.format(DateTimeFormatter.ofPattern("HHmm"));
			timeSlots.add(timeSlot);
			currentTime = nextTime;
		}

		// Write the availability to Doctor_Availability.csv, ensuring each new entry
		// starts on a new line
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Doctor_Availability.csv", true))) {
			File availabilityFile = new File("data/Doctor_Availability.csv");

			// Check if the file is not empty and if the last line has a trailing newline
			if (availabilityFile.length() > 0) {
				try (RandomAccessFile fileHandler = new RandomAccessFile(availabilityFile, "r")) {
					fileHandler.seek(availabilityFile.length() - 1);
					int lastChar = fileHandler.read();
					if (lastChar != '\n') {
						writer.newLine(); // Add a newline if not present
					}
				}
			}

			// Append each time slot for the selected date
			for (String timeSlot : timeSlots) {
				writer.write(String.join(",", doctorID, date.format(dateFormatter), timeSlot, "Available"));
				writer.newLine(); // Ensure each entry starts on a new row
			}
			System.out.println("Availability set successfully for " + date.format(dateFormatter) + ".\n");
		} catch (IOException e) {
			System.out.println("Error writing to Doctor_Availability.csv: " + e.getMessage());
		}
	}

	public static void approveOrDisapproveAppointments(String doctorID, Scanner scanner) {
	    List<String[]> pendingAppointments = new ArrayList<>();

	    // Read from Patient_Appointment.csv to find pending appointments for the given doctor
	    try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
	        String line;
	        br.readLine(); // Skip the header
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            String appointmentDoctorID = data[2].trim();
	            String status = data[5].trim();

	            if (appointmentDoctorID.equals(doctorID) && status.equalsIgnoreCase("Pending")) {
	                pendingAppointments.add(data);
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading Patient_Appointment.csv: " + e.getMessage());
	        return;
	    }

	    if (pendingAppointments.isEmpty()) {
	        System.out.println("No pending appointments found for Doctor ID: " + doctorID);
	        return;
	    }

	    System.out.println("=========================================");
	    System.out.println("         Pending Appointment Slots       ");
	    System.out.println("=========================================");

	    for (String[] appointment : pendingAppointments) {
	        String appointmentID = appointment[0];
	        String patientID = appointment[1];
	        String date = appointment[3];
	        String timeSlot = appointment[4];

	        System.out.printf("Patient ID       : %s%n", patientID);
	        System.out.printf("Appointment ID   : %s%n", appointmentID);
	        System.out.printf("Date             : %s%n", date);
	        System.out.printf("Time Slot        : %s%n", timeSlot);
	        System.out.println();
	    }

	    System.out.print("Select Appointment ID to approve/decline: ");
	    String selectedAppointmentID = scanner.nextLine().trim();

	    // Find the selected appointment to update its status
	    String[] selectedAppointment = pendingAppointments.stream()
	            .filter(app -> app[0].equals(selectedAppointmentID))
	            .findFirst()
	            .orElse(null);

	    if (selectedAppointment == null) {
	        System.out.println("Invalid Appointment ID selected.");
	        return;
	    }

	    System.out.print("Do you want to approve this appointment? (y/n): ");
	    String decision = scanner.nextLine().trim().toLowerCase();

	    if (decision.equals("y")) {
	        updateAppointmentStatus(selectedAppointmentID, "Confirmed");
	        System.out.println("Appointment confirmed successfully.\n");
	    } else if (decision.equals("n")) {
	        updateAppointmentStatus(selectedAppointmentID, "Cancelled by Doctor");
	        // Update the availability back to "Available"
	        String date = selectedAppointment[3];
	        String timeSlot = selectedAppointment[4];
	        updateDoctorAvailability(selectedAppointment[2], date, timeSlot, "Available");
	        System.out.println("Appointment declined and slot made available again.\n");
	    } else {
	        System.out.println("Invalid input. Please enter 'y' or 'n'.");
	    }
	}

	private static void updateAppointmentStatus(String appointmentID, String newStatus) {
	    List<String[]> updatedAppointments = new ArrayList<>();
	    boolean statusUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Appointment.csv"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            if (data[0].trim().equals(appointmentID)) {
	                data[5] = newStatus; // Update the status field
	                statusUpdated = true;
	            }
	            updatedAppointments.add(data);
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading Patient_Appointment.csv: " + e.getMessage());
	        return;
	    }

	    // Write the updated data back to the CSV file
	    if (statusUpdated) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Patient_Appointment.csv"))) {
	            for (String[] record : updatedAppointments) {
	                writer.write(String.join(",", record));
	                writer.newLine();
	            }
	        } catch (IOException e) {
	            System.out.println("Error writing to Patient_Appointment.csv: " + e.getMessage());
	        }
	    }
	}

	private static void updateDoctorAvailability(String doctorID, String date, String timeSlot, String newStatus) {
	    List<String[]> updatedAvailability = new ArrayList<>();
	    boolean slotUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader("data/Doctor_Availability.csv"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");
	            if (data[0].trim().equals(doctorID) && data[1].trim().equals(date) && data[2].trim().equals(timeSlot)) {
	                data[3] = newStatus; // Update the status field to "Available"
	                slotUpdated = true;
	            }
	            updatedAvailability.add(data);
	        }
	    } catch (IOException e) {
	        System.out.println("Error reading Doctor_Availability.csv: " + e.getMessage());
	    }

	    // Write the updated availability back to the CSV file
	    if (slotUpdated) {
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/Doctor_Availability.csv"))) {
	            for (String[] record : updatedAvailability) {
	                writer.write(String.join(",", record));
	                writer.newLine();
	            }
	            System.out.println("Doctor availability updated successfully.");
	        } catch (IOException e) {
	            System.out.println("Error writing to Doctor_Availability.csv: " + e.getMessage());
	        }
	    }
	}
	
	

}
