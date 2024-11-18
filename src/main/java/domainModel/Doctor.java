package domainModel;

import java.util.ArrayList;

public class Doctor extends Employee {
    private String medicalLicenseNumber;
    private ArrayList<MedicalExam> medicalExamList;


    public Doctor() {

    }

    public Doctor(String name, String surname, String dateOfBirth, String iban, int id, String medicalLicenseNumber) {
        super(name, surname, dateOfBirth, iban, id);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }
    public ArrayList<MedicalExam> getMedicalExamsList() {
        return medicalExamList;
    }
    public void setMedicalExamsList(ArrayList<MedicalExam> medicalExamList) {
        this.medicalExamList = medicalExamList;
    }

}
