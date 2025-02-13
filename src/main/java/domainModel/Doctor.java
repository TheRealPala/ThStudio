package domainModel;

import java.util.ArrayList;

public class Doctor extends Person {
    private String medicalLicenseNumber;


    public Doctor(String name, String surname, String dateOfBirth, int id, String medicalLicenseNumber, double balance) {
        super(id, name, surname, dateOfBirth, balance);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public Doctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber) {
        super(name, surname, dateOfBirth);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

}
