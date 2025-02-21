package com.thstudio.project.domainModel;

public class Doctor extends Person {
    private String medicalLicenseNumber;


    public Doctor(String name, String surname, String dateOfBirth, int id, String medicalLicenseNumber, double balance) {
        super(id, name, surname, dateOfBirth, balance);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public Doctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance) {
        super(name, surname, dateOfBirth, balance);
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Doctor doctor = (Doctor) o;
        return doctor.id == this.id && doctor.name.equals(this.name) && doctor.surname.equals(this.surname) &&
                doctor.dateOfBirth.equals(this.dateOfBirth) && doctor.balance == this.balance &&
                doctor.medicalLicenseNumber.equals(this.medicalLicenseNumber);
    }
}
