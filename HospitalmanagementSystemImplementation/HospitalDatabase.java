package HospitalmanagementSystemImplementation;

import java.sql.*;

public class HospitalDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASSWORD = "Aspire@123"; // Change to your MySQL password
    private Connection connection;

    // Constructor to establish the database connection
    public HospitalDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.err.println("Unable to connect to database: " + e.getMessage());
        }
    }

    // Method to add a doctor to the database
    public void addDoctor(Doctor doctor) {
        String query = "INSERT INTO doctors (name, specialization, phone, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getPhone());
            stmt.setString(4, doctor.getAddress());
            stmt.executeUpdate();
            System.out.println("Doctor added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding doctor: " + e.getMessage());
        }
    }

    // Method to add a patient to the database
    public void addPatient(Patient patient) {
        String query = "INSERT INTO patients (name, phone, address, patient_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getPhone());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPatientId());
            stmt.executeUpdate();
            System.out.println("Patient added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding patient: " + e.getMessage());
        }
    }


    // Method to add an appointment to the database
    public void addAppointment(Appointment appointment) {
        String query = "INSERT INTO appointments (doctor_id, patient_id, appointment_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, getDoctorId(appointment.getDoctor()));  // Get doctor_id by doctor name
            stmt.setInt(2, getPatientId(appointment.getPatient()));  // Get patient_id by patient_id field
            stmt.setDate(3, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.executeUpdate();
            System.out.println("Appointment scheduled successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding appointment: " + e.getMessage());
        }
    }

    // Method to get the next available patient ID (incremental based on the highest ID)
    public int getNextPatientId() {
        String query = "SELECT MAX(CAST(SUBSTRING(patient_id, 2) AS UNSIGNED)) AS max_patient_id FROM patients";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("max_patient_id") + 1; // Increment the max patient ID found in the database
            } else {
                return 1; // If no patients exist, start from 1
            }
        } catch (SQLException e) {
            System.err.println("Error getting next patient ID: " + e.getMessage());
            return 1; // If there’s an error, return 1 to start from the first ID
        }
    }

    // Refactored method to fetch doctor or patient ID by name or ID
    private int getIdByName(String tableName, String name, String idColumn) {
        String query = "SELECT id FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ID for " + tableName + ": " + e.getMessage());
        }
        return -1;  // Return -1 if not found
    }

    // Fetch doctor ID
    private int getDoctorId(Doctor doctor) {
        return getIdByName("doctors", doctor.getName(), "id");
    }

    // Fetch patient ID
    private int getPatientId(Patient patient) {
        return getIdByName("patients", patient.getPatientId(), "id");
    }

    // Method to fetch all doctors from the database
    public ResultSet getAllDoctors() {
        String query = "SELECT id, name, specialization FROM doctors";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query); // Return a fresh ResultSet each time it's called
        } catch (SQLException e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
            return null;
        }
    }

    // Method to fetch a doctor by index (used for doctor selection)
    public Doctor getDoctorByIndex(int index) {
        String query = "SELECT id, name, specialization, phone, address FROM doctors LIMIT ?, 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, index - 1);  // Index is 1-based, so subtract 1 for 0-based query
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(rs.getString("name"), rs.getString("specialization"), 
                    rs.getString("phone"), rs.getString("address"));
            } else {
                System.out.println("Doctor not found.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor by index: " + e.getMessage());
            return null;
        }
    }

    // Method to get a patient by ID
    public Patient getPatientById(String patientId) {
        String query = "SELECT name, phone, address FROM patients WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Patient(rs.getString("name"), rs.getString("phone"), 
                    rs.getString("address"), patientId);
            } else {
                System.out.println("Patient not found.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient by ID: " + e.getMessage());
            return null;
        }
    }

    // Close database connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
