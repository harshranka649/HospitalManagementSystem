package HospitalmanagementSystemImplementation;

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HospitalManagementSystemMain {
	public static void main(String[] args) {
	    Scanner scanner = new Scanner(System.in);
	    HospitalDatabase database = null;

	    try {
	        // Establish database connection
	        database = new HospitalDatabase();

	        // Insert preset doctors into the database only if they don't already exist
	        insertPresetDoctors(database);

	        // Fetch all doctors once and store them in a list
	        List<Doctor> doctorsList = getDoctorsList(database); // Fetch doctors only once

	        boolean exit = false;
	        while (!exit) {
	            System.out.println("\n--- Hospital Management System ---");
	            System.out.println("1. Add Doctor");
	            System.out.println("2. Add Patient");
	            System.out.println("3. Schedule Appointment");
	            System.out.println("4. Exit");

	            System.out.print("Enter your choice: ");
	            int choice = getIntInput(scanner);
	            scanner.nextLine(); // To consume newline left by nextInt()

	            // Declare necessary variables only once
	            String name = "", phone = "", address = "", patientIdInput = ""; 

	            switch (choice) {
	                case 1:
	                    if (isAdminAuthenticated(scanner)) {
	                        System.out.print("Enter doctor's name: ");
	                        name = getStringInput(scanner);
	                        System.out.print("Enter specialization: ");
	                        String specialization = getSpecializationInput(scanner);
	                        System.out.print("Enter phone: ");
	                        phone = getPhoneInput(scanner);
	                        System.out.print("Enter address: ");
	                        address = scanner.nextLine();
	                        Doctor doctor = new Doctor(name, specialization, phone, address);
	                        database.addDoctor(doctor);
	                    }
	                    break;
	                case 2:
	                    System.out.print("Enter patient's name: ");
	                    name = getStringInput(scanner);
	                    System.out.print("Enter phone: ");
	                    phone = getPhoneInput(scanner);
	                    System.out.print("Enter address: ");
	                    address = scanner.nextLine();
	                    System.out.print("Are you a new patient? (yes/no): ");
	                    String isNewPatient = scanner.nextLine();

	                    if (isNewPatient.equalsIgnoreCase("yes")) {
	                        patientIdInput = generatePatientId(database); // Generate new patient ID
	                        System.out.println("New Patient ID generated: " + patientIdInput);
	                    } else {
	                        System.out.print("Enter your existing patient ID: ");
	                        patientIdInput = scanner.nextLine(); // Enter existing patient ID
	                    }
	                    Patient patient = new Patient(name, phone, address, patientIdInput);
	                    database.addPatient(patient);
	                    break;
	                case 3:
	                    System.out.print("Are you a new patient? (yes/no): ");
	                    String isNewPatientAppointment = scanner.nextLine();

	                    if (isNewPatientAppointment.equalsIgnoreCase("yes")) {
	                        // For a new patient, generate a new patient ID
	                        patientIdInput = generatePatientId(database); 
	                        System.out.println("New Patient ID generated: " + patientIdInput);

	                        // Collect patient details (name, phone, address) for new patient
	                        System.out.print("Enter patient's name: ");
	                        name = getStringInput(scanner);  // Assigning values to already declared variables
	                        System.out.print("Enter phone: ");
	                        phone = getPhoneInput(scanner);  // Assigning values to already declared variables
	                        System.out.print("Enter address: ");
	                        address = scanner.nextLine();    // Assigning values to already declared variables

	                        // Create the new patient and add to the database
	                        Patient newPatient = new Patient(name, phone, address, patientIdInput);
	                        database.addPatient(newPatient);  // Save the new patient to the database
	                    } else {
	                        // For an existing patient, just get the patient ID
	                        System.out.print("Enter your existing patient ID: ");
	                        patientIdInput = scanner.nextLine();  // Assign the value to the already declared variable
	                    }

	                    // Now display available doctors for the appointment
	                    System.out.println("\n--- Available Doctors ---");
	                    int index = 1;
	                    for (Doctor doctor : doctorsList) {
	                        System.out.println(index + ". Dr. " + doctor.getName() + " - " + doctor.getSpecialization());
	                        index++;
	                    }

	                    // Ask the patient to select a doctor
	                    System.out.print("\nSelect a doctor by number: ");
	                    int doctorChoice = getIntInput(scanner);
	                    scanner.nextLine(); // Consume newline

	                    // Get the selected doctor from the list
	                    Doctor selectedDoctor = doctorsList.get(doctorChoice - 1); // Doctor list is 0-indexed

	                    if (selectedDoctor != null) {
	                        // Get the appointment date
	                        System.out.print("Enter appointment date (YYYY-MM-DD): ");
	                        String date = scanner.nextLine();
	                        LocalDate appointmentDate = null;

	                        try {
	                            appointmentDate = LocalDate.parse(date);

	                            LocalDate currentDate = LocalDate.now();
	                            if (appointmentDate.isBefore(currentDate)) {
	                                System.out.println("Error: You cannot schedule an appointment for a past date.");
	                                break;
	                            }
	                        } catch (Exception e) {
	                            System.out.println("Error: Invalid date format.");
	                            break;
	                        }

	                        // Fetch the patient object (whether new or existing)
	                        Patient patientObj = database.getPatientById(patientIdInput);
	                        if (patientObj != null) {
	                            Appointment appointment = new Appointment(patientObj, selectedDoctor, java.sql.Date.valueOf(appointmentDate));
	                            database.addAppointment(appointment);
	                            System.out.println("Appointment scheduled successfully!");
	                        } else {
	                            System.out.println("Error: Patient not found.");
	                        }
	                    }
	                    break;

	                case 4:
	                    exit = true;
	                    System.out.println("Exiting system...");
	                    break;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        }

	    } finally {
	        if (database != null) {
	            database.close();
	        }
	        scanner.close();
	    }
	}

    // Method to fetch the list of doctors from the database (only once)
    private static List<Doctor> getDoctorsList(HospitalDatabase database) {
        List<Doctor> doctorsList = new ArrayList<>();
        ResultSet doctors = null;
        try {
            doctors = database.getAllDoctors(); // Fetch doctors from the database
            while (doctors.next()) {
                String doctorName = doctors.getString("name");
                String doctorSpecialization = doctors.getString("specialization");
                Doctor doctor = new Doctor(doctorName, doctorSpecialization);
                doctorsList.add(doctor);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
        } finally {
            if (doctors != null) {
                try {
                    doctors.close();
                } catch (SQLException e) {
                    System.err.println("Error closing ResultSet: " + e.getMessage());
                }
            }
        }
        return doctorsList;
    }

    // Method to handle input for integer type
    private static int getIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // consume the invalid input
        }
        return scanner.nextInt();
    }

    // Method to handle string input (trim whitespace and check empty)
    private static String getStringInput(Scanner scanner) {
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.println("Input cannot be empty. Please enter again:");
            input = scanner.nextLine().trim();
        }
        return input;
    }

    // Helper methods to get specialization and phone input
    private static String getSpecializationInput(Scanner scanner) {
        System.out.print("Enter specialization: ");
        return scanner.nextLine().trim();
    }

    private static String getPhoneInput(Scanner scanner) {
        System.out.print("");
        return scanner.nextLine().trim();
    }

    // Admin authentication method
    private static boolean isAdminAuthenticated(Scanner scanner) {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        char[] passwordArray = System.console().readPassword();
        String password = new String(passwordArray);
        return username.equals("admin") && password.equals("admin@234");
    }

    // Method to generate new patient ID
    private static String generatePatientId(HospitalDatabase database) {
        String prefix = "P";
        int nextId = database.getNextPatientId();
        return prefix + String.format("%03d", nextId); // Generate new patient ID with the format P001, P002, etc.
    }

    // Method to insert preset doctors into the database (for initial setup)
    private static void insertPresetDoctors(HospitalDatabase database) {
        // Preset doctor data (modify as needed)
        Doctor[] presetDoctors = {
            new Doctor(" John Doe", "Cardiologist", "1234567890", "123 Main St."),
            new Doctor(" Jane Smith", "Neurologist", "0987654321", "456 Oak St."),
            new Doctor(" Ramesh", "General Physician", "1122334455", "789 Pine St.")
        };

        for (Doctor doctor : presetDoctors) {
            database.addDoctor(doctor);
        }
    }
}
