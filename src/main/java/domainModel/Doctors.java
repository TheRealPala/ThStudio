package domainModel;

import java.util.ArrayList;

public class Doctors extends Employee {
    private String medicalLicenseNumber;


    public Doctors() {

    }

    public Doctors(String name, String surname, String dateOfBirth, String iban, int id, String medicalLicenseNumber) {
        super(name, surname, dateOfBirth, iban, id);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }


}
