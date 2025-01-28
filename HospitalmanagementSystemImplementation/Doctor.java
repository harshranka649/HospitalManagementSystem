package HospitalmanagementSystemImplementation;

public class Doctor {
    private String name;
    private String specialization;
    private String phone;
    private String address;

    // Constructor for creating a doctor with all details (name, specialization, phone, address)
    public Doctor(String name, String specialization, String phone, String address) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.address = address;
    }

    // Constructor for creating a doctor with just name and specialization
    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
        this.phone = ""; // Default value or can be set as needed
        this.address = ""; // Default value or can be set as needed
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Dr. " + name + " - " + specialization;
    }
}
