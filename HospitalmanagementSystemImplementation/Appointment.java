package HospitalmanagementSystemImplementation;
import java.util.Date;

public class Appointment {
    private Patient patient;
    private Doctor doctor;
    private Date appointmentDate;

    public Appointment(Patient patient, Doctor doctor, Date appointmentDate) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }
    
    

    @Override
    public String toString() {
        return "Appointment for " + patient.getName() + " with Dr. " + doctor.getName() + " on " + appointmentDate;
    }
}