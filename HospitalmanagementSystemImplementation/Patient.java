package HospitalmanagementSystemImplementation;

public class Patient {
    private String name;
    private String phone;
    private String address;
    private String patientId;

    // Constructor for creating a new patient without passing the patient_id
    public Patient(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.patientId = generatePatientId();  // Generate patient ID automatically (optional)
    }

    // If the patient_id is manually passed (for existing patients)
    public Patient(String name, String phone, String address, String patientId) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPatientId() {
        return patientId;
    }

    // Optional: A method to generate patient ID if you want it in your code (otherwise let DB handle it)
    private String generatePatientId() {
        return "P" + (int)(Math.random() * 10000);  // Example of generating a random ID
    }
}

