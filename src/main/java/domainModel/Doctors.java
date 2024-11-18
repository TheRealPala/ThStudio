package domainModel;

import java.util.ArrayList;

public class Doctors extends Employee {
    private String medicalLicenseNumber;
    private ArrayList<MedicalExams> medicalExamsList;


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
    public ArrayList<MedicalExams> getMedicalExamsList() {
        return medicalExamsList;
    }
    public void setMedicalExamsList(ArrayList<MedicalExams> medicalExamsList) {
        this.medicalExamsList = medicalExamsList;
    }

}
