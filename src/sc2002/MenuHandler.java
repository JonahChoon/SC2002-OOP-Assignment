package sc2002;

public class MenuHandler {

    // Utility method to print a separator line
    private static void printSeparator() {
        System.out.println("=========================================");
    }

    // Utility method to print a menu header with title
    private static void printHeader(String title) {
        printSeparator();
        System.out.println("\t   " + title);
        printSeparator();
    }

    // Menu for Administrator
    public static void displayAdministratorMenu() {
        PrescriptionHandler.viewLowStockWarnings();
        printHeader("Administrator Menu");
        System.out.println("→ 1. View and Manage Hospital Staff");
        System.out.println("→ 2. View Appointments details");
        System.out.println("→ 3. View and Manage Medication Inventory");
        System.out.println("→ 4. Approve Replenishment Requests");
        System.out.println("→ 5. View Login Logs File");
        System.out.println("→ 6. Logout");
        printSeparator();
        System.out.print("\nChoose an option: ");
    }

    // Menu for Doctor
    public static void displayDoctorMenu() {
        printHeader("   Doctor Menu");
        System.out.println("→ 1. View Patient Medical Records");
        System.out.println("→ 2. Update Patient Medical Records");
        System.out.println("→ 3. View Personal Schedule");
        System.out.println("→ 4. Set Availability for Appointments");
        System.out.println("→ 5. Accept or Decline Appointment Requests");
        System.out.println("→ 6. View Upcoming Appointments");
        System.out.println("→ 7. Record Appointment Outcome");
        System.out.println("→ 8. Logout");
        printSeparator();
        System.out.print("\nChoose an option: ");
    }

    // Menu for Patient
    public static void displayPatientMenu() {
        printHeader("  Patient Menu");
        System.out.println("→ 1. View Medical Record");
        System.out.println("→ 2. Update Personal Information");
        System.out.println("→ 3. View Available Appointment Slots");
        System.out.println("→ 4. Schedule an Appointment");
        System.out.println("→ 5. Reschedule an Appointment");
        System.out.println("→ 6. Cancel an Appointment");
        System.out.println("→ 7. View Scheduled Appointments");
        System.out.println("→ 8. View Past Appointment Outcome Records");
        System.out.println("→ 9. Logout");
        printSeparator();
        System.out.print("\nChoose an option: ");
    }

    // Menu for Pharmacist
    public static void displayPharmacistMenu() {
    	PrescriptionHandler.viewLowStockWarnings();
        printHeader(" Pharmacist Menu");
        System.out.println("→ 1. View Appointment Outcome Record");
        System.out.println("→ 2. Update Prescription Status");
        System.out.println("→ 3. View Medication Inventory");
        System.out.println("→ 4. Submit Replenishment Request");
        System.out.println("→ 5. Logout");
        printSeparator();
        System.out.print("\nChoose an option: ");
    }
}
