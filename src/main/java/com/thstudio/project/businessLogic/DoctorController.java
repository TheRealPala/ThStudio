package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;

public class DoctorController extends PersonController<Doctor> {
    private DoctorDao doctorDao;

    public DoctorController(DoctorDao doctorDao) {
        super(doctorDao);
        this.doctorDao = doctorDao;
    }

    /**
     * Add a new doctor
     *
     * @param name                 The name of the doctor
     * @param surname              The surname of the doctor
     * @param dateOfBirth          The date of birth of the doctor
     * @param medicalLicenseNumber The medical license number of the doctor
     * @param balance              The balance of the doctor
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public String addDoctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, medicalLicenseNumber, balance);
        return super.addPerson(d);
    }

    public void updateDoctor(Doctor doctor) throws Exception {
        doctorDao.update(doctor);
    }

}
