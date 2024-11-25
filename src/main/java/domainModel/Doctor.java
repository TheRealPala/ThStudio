package domainModel;

import java.util.ArrayList;

public class Doctor extends Person {
    private String medicalLicenseNumber;


    public Doctor(String name, String surname, String dateOfBirth, String iban, int id, String medicalLicenseNumber) {
        super(id, name, surname, dateOfBirth, iban);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public Doctor(String name, String surname, String dateOfBirth, String iban, String medicalLicenseNumber) {
        super(name, surname, dateOfBirth, iban);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

}
