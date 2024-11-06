package sc2002;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles the viewing and management of patient medical records.
 */
public class MedicalRecordHandler {

	/**
	 * Views the medical record of a specific patient by their ID.
	 *
	 * @param patientID The ID of the patient whose medical record is to be viewed.
	 */
	public static void viewMedicalRecord(String patientID) {
		try {
			Patient patient = getPatientDetails(patientID);
			if (patient == null) {
				System.out.println("No patient found with the given ID.");
				return;
			}

			List<MedicalHistory> medicalHistory = getPatientMedicalHistory(patientID);

			displayPatientDetails(patient);

			if (medicalHistory.isEmpty()) {
				System.out.println("\nNo past diagnosis or treatments found.");
			} else {
				displayMedicalHistory(medicalHistory);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the details of a patient by their ID.
	 *
	 * @param patientID The ID of the patient.
	 * @return A {@code Patient} object if found, otherwise {@code null}.
	 */
	private static Patient getPatientDetails(String patientID) {
		String line;
		String splitBy = ",";
		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Records.csv"))) {
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data.length == 7 && data[0].equals(patientID)) {
					return new Patient(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves the medical history of a patient by their ID.
	 *
	 * @param patientID The ID of the patient.
	 * @return A list of {@code MedicalHistory} objects representing the patient's
	 *         medical history.
	 */
	static List<MedicalHistory> getPatientMedicalHistory(String patientID) {
		String line;
		String splitBy = ",";
		List<MedicalHistory> historyList = new ArrayList<>();
		Map<String, String> doctors = Doctor.loadDoctorNamesFromCSV("data/Staff_Records.csv");

		try (BufferedReader br = new BufferedReader(new FileReader("data/Patient_Diagnosis_Prescription.csv"))) {
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (data[0].equals(patientID)) {
					String doctorID = data[8].trim();
					String doctorName = doctors.getOrDefault(doctorID, "Unknown Doctor");

					String caseNotes = data[6].trim(); // Assuming case notes are in column 7 (index 6)

					MedicalHistory history = new MedicalHistory(data[1], // Date
							data[9], // Appointment ID
							data[10], // Type of Service
							data[2], // Diagnosis
							data[3], // Treatment
							caseNotes, // Case Notes
							doctorName, // Doctor Name
							doctorID // Doctor ID
					);

					historyList.add(history);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return historyList;
	}

	/**
	 * Displays the details of a patient.
	 *
	 * @param patient The {@code Patient} object whose details are to be displayed.
	 */
	public static void displayPatientDetails(Patient patient) {
		String separator = "─────────────────────────────────────────";
		System.out.println(separator);
		System.out.printf("%s [%s]%n", patient.getUserName(), patient.getPatientID());
		System.out.println(separator);
		System.out.printf("%-20s: %s%n", "Date of Birth", patient.getDateOfBirth());
		System.out.printf("%-20s: %s%n", "Gender", patient.getGender());
		System.out.printf("%-20s: %s%n", "Email Address", patient.getEmailAddress());
		System.out.printf("%-20s: %s%n", "Phone Number", patient.getPhoneNumber());
		System.out.printf("%-20s: %s%n", "Blood Type", patient.getBloodType());
		System.out.println(separator);
		System.out.println();
	}

	/**
	 * Displays a list of medical history records.
	 *
	 * @param historyList A list of {@code MedicalHistory} objects to be displayed.
	 */
	static void displayMedicalHistory(List<MedicalHistory> historyList) {
		String separator = "─────────────────────────────────────────";
		for (MedicalHistory history : historyList) {
			System.out.println(separator);
			System.out.printf("%-20s: %s%n", "Date", history.getDate());
			System.out.printf("%-20s: %s [%s]%n", "Doctor Name [ID]", history.getDoctorName(), history.getDoctorID());
			System.out.printf("%-20s: %s%n", "Appointment ID", history.getAppointmentID());
			System.out.printf("%-20s: %s%n", "Type of Service", history.getTypeOfService());
			System.out.printf("%-20s: %s%n", "Diagnosis", history.getPastDiagnosis());
			System.out.printf("%-20s: %s%n", "Treatment", history.getTreatment());
			System.out.printf("%-20s: %s%n", "Case Notes", history.getCaseNotes());
			System.out.println(separator);
			System.out.println();
		}
	}

	/**
	 * Inner class representing a patient's medical history record.
	 */
	static class MedicalHistory {
		private String date;
		private String appointmentID;
		private String typeOfService;
		private String pastDiagnosis;
		private String treatment;
		private String caseNotes;
		private String doctorName;
		private String doctorID;

		/**
		 * Constructor for {@code MedicalHistory}.
		 *
		 * @param date          The date of the medical history record.
		 * @param appointmentID The ID of the appointment associated with the record.
		 * @param typeOfService The type of service provided.
		 * @param pastDiagnosis The diagnosis made during the appointment.
		 * @param treatment     The treatment provided during the appointment.
		 * @param caseNotes     Notes associated with the appointment.
		 * @param doctorName    The name of the doctor who attended the appointment.
		 * @param doctorID      The ID of the doctor.
		 */
		public MedicalHistory(String date, String appointmentID, String typeOfService, String pastDiagnosis,
				String treatment, String caseNotes, String doctorName, String doctorID) {
			this.date = date;
			this.appointmentID = appointmentID;
			this.typeOfService = typeOfService;
			this.pastDiagnosis = pastDiagnosis;
			this.treatment = treatment;
			this.caseNotes = caseNotes;
			this.doctorName = doctorName;
			this.doctorID = doctorID;
		}

		/**
		 * Gets the date of the appointment.
		 *
		 * @return The date of the appointment.
		 */
		public String getDate() {
			return date;
		}

		/**
		 * Gets the unique identifier for the appointment.
		 *
		 * @return The appointment ID.
		 */
		public String getAppointmentID() {
			return appointmentID;
		}

		/**
		 * Gets the type of service provided during the appointment.
		 *
		 * @return The type of service.
		 */
		public String getTypeOfService() {
			return typeOfService;
		}

		/**
		 * Gets the past diagnosis associated with the appointment.
		 *
		 * @return The past diagnosis.
		 */
		public String getPastDiagnosis() {
			return pastDiagnosis;
		}

		/**
		 * Gets the treatment prescribed or provided during the appointment.
		 *
		 * @return The treatment.
		 */
		public String getTreatment() {
			return treatment;
		}

		/**
		 * Gets the case notes recorded for the appointment.
		 *
		 * @return The case notes.
		 */
		public String getCaseNotes() {
			return caseNotes;
		}

		/**
		 * Gets the name of the doctor handling the appointment.
		 *
		 * @return The doctor's name.
		 */
		public String getDoctorName() {
			return doctorName;
		}

		/**
		 * Gets the unique identifier for the doctor handling the appointment.
		 *
		 * @return The doctor ID.
		 */
		public String getDoctorID() {
			return doctorID;
		}
	}
}
