package com.thstudio.project.fixture;

import com.github.javafaker.Faker;
import com.thstudio.project.domainModel.Doctor;


public class DoctorFixture {
    private static final Faker faker = new Faker();

    public static Doctor genDoctor() {
        Doctor doctor = new Doctor(PersonFixture.genPerson());
        doctor.setMedicalLicenseNumber(faker.regexify("^MLN-\\d{5}$\n"));
        return doctor;
    }

    public static Doctor genTestDoctor() {
        Doctor doctor = new Doctor(PersonFixture.genTestPerson());
        doctor.setMedicalLicenseNumber(faker.regexify("^MLN-\\d{5}$\n"));
        return doctor;
    }
}
