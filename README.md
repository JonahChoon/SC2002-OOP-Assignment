![Banner](banner.png)
<h1 align="center">
  <br>
  Hospital Management System
  <br>
</h1>
<h4 align="center">SC/CE/CZ2002 Object-Oriented Design & Programming</h4>


<p align="center">
  <a href="#overview">Overview</a> •
  <a href="#table-of-contents">Table of Contents</a> •
  <a href="#1-setup-and-usage">Setup</a> •
  <a href="#2-project-structure">Structure</a> •
  <a href="#3-assumptions-and-constraints">Constraints</a>  •
  <a href="#4-test-cases">Test Cases</a>
</p>



## Overview
The Hospital Management System (HMS) is a command-line application built using Java, designed to automate and streamline hospital operations, including patient management, appointment scheduling, staff management, and billing. This project emphasizes the application of Object-Oriented Design principles and collaborative development practices.
* Objectives
    - Apply Object-Oriented (OO) concepts to model and design a Java-based application.
    - Gain experience in collaborative programming to achieve a unified system design.
    - Develop a CLI-based Hospital Management System with essential features.

## Table of Contents
1. <a href = "#1-setup-and-usage">Setup & Usage</a>
2. <a href = "#2-project-structure">Project Structure</a>
3. <a href = "#3-assumptions-and-constraints">Assumptions & Constraints</a>
4. <a href="#4-test-cases">Test Cases</a>

## 1. Setup and Usage

Follow these steps to set up the Hospital Management System on your local machine.

### 1.1 Prerequisites

- **Java Development Kit (JDK 8 or higher)**
- **Optional**: An IDE like IntelliJ IDEA, Eclipse, or Visual Studio Code

### 1.2 Installation Steps

1.2.1. **Clone the Repository**
   ```bash
   git clone https://github.com/JonahChoon/SC2002-OOP-Assignment.git
   ```
   Or download as a ZIP and extract it.

1.2.2. **Navigate to the Project Directory**
   ```bash
   cd SC2002-OOP-Assignment
   ```

1.2.3. **Run the Application**
   ```bash
   java -cp bin main
   ```
   - Make sure to run this from the project's root directory.

### 1.3 Using an IDE
- Open the project in your preferred IDE.
- Ensure the JDK is correctly configured.
- Run the `Main.java` class.

---

## 1.4 Usage

1.4.1. **Login**: Enter your Staff ID and Password.
   - **Note**: Change the default password after the first login
   - **Default**: `password`

1.4.2. **Role-Based Access**:
   - **Patient**: View records, update info, manage appointments.
   - **Doctor**: Access/update records, manage schedules.
   - **Pharmacist**: Manage prescriptions and inventory.
   - **Admin**: Manage users, appointments, and inventory requests.

1.4.3. **Navigation**: Follow on-screen prompts and enter numbers/commands as needed.

## 2. Project Structure
    SC2002-OOP-Assignment
    ├───JavaDoc
    ├───bin
    │   └───sc2002
    ├───data
    │   ├── Doctor_Availability.csv 📅
    │   ├── Login_Activity.csv 🔐
    │   ├── Master_LoginData.csv 🔒
    │   ├── Medicine_Request.csv 💊
    │   ├── Medicine_Stock.csv 📦
    │   ├── Patient_Appointment.csv 🏥
    │   ├── Patient_Diagnosis_Prescription.csv 📝
    │   ├── Patient_Records.csv 📂
    │   └── Staff_Records.csv 🧑‍⚕️
    ├───banner.png
    ├───Test Case Showcase     
    │   ├── Test Case Guide // Document detailing HMS test cases, with detailed input and output
    │   └── Test Case Video.txt
    └───src
        └───sc2002
            ├── Administrator.java          // Represents an Administrator user who can manage various tasks within the system.
            ├── AppointmentSlotHandler.java // This class handles various operations related to appointment slots, including viewing, scheduling, rescheduling, and cancelling appointments.
            ├── Doctor.java                 // Represents a Doctor in the medical system, inheriting from the User class.
            ├── DoctorRecordHandler.java    // Handles operations related to doctor records, including retrieval and updates.
            ├── HospitalStaffHandler.java   // Provides functionalities to manage hospital staff such as adding, updating, viewing, and deleting staff records.
            ├── LoginHandler.java           // Handles user authentication and login activities within the system, including password management and activity logging.
            ├── Main.java                   // Entry point for the hospital management system application.
            ├── MedicalRecordHandler.java   // Handles the viewing and management of patient medical records.
            ├── MenuHandler.java            // Handles the display of various user role menus in the Hospital Management System.
            ├── Patient.java                // Represents a patient in the hospital management system.
            ├── PersonalInfoHandler.java    // Handles the updating of personal information for patients.
            ├── Pharmacist.java             // Represents a Pharmacist user in the hospital management system.
            ├── PrescriptionHandler.java    // Handles prescription management, including viewing, updating, and managing medicine stock and requests.
            └── User.java                   // Represents a general user in the hospital management system.

## 3. Assumptions and Constraints
    a)	No excel files are opened while the HMS is running (E.g. Patient_Records.csv)
    b)	There is at least one Patient, Doctor, Pharmacist and Administrator in the HMS at all times
    c)	Each Patient’s appointment with a Doctor is 1 hour long (E.g. 0900 to 1000 hrs)
    d)   Each Patient will only book maximum 1 appointment per day with each Doctor
    e)	Doctors do not take last-minute MC or leave
    f)	Patients receive prescription on same day of appointment
    g)	Low medicine stock is approved on same day of request
    h)	Any Doctor removed by Administrator do not have any upcoming appointments with any Patient

## 4. Test Cases
   - 4.1 Kindly refer to the "Test Case Showcase" folder for video and test case guide on the full breakdown of the required test cases
   - 4.2 <a href="https://entuedu-my.sharepoint.com/:v:/g/personal/jchoon001_e_ntu_edu_sg/EQL8dz0N641Ei1W9d871tMABkQRx7WoDq4U-9rU-E9Qkrw?nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&e=ig8S6K
">Link to Test Case Video</a>
