package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;

public class DoctorController extends PersonController<Doctor> {
    private final DoctorDao doctorDao;

    public DoctorController(DoctorDao doctorDao) {
        super(doctorDao);
        this.doctorDao = doctorDao;
    }

    /**
     * Add a new doctor
     *
     * @param name                 The name of the doctor
     * @param surname              The surname of the doctor
     * @param dateOfBirth          The birthdate of the doctor
     * @param medicalLicenseNumber The medical license number of the doctor
     * @param balance              The balance of the doctor
     * @return The added doctor
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Doctor addDoctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, medicalLicenseNumber, balance);
        super.addPerson(d);
        return d;
    }

    /**
     * Add a new doctor from obj
     *
     * @param doctor The doctor obk
     * @return The added doctor
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Doctor addDoctor(Doctor doctor) throws Exception {
        return this.addDoctor(doctor.getName(), doctor.getSurname(), doctor.getDateOfBirth(), doctor.getMedicalLicenseNumber(), doctor.getBalance());
    }

    /**
     * get the doctor
     *
     * @param id The id of the doctor
     * @return The customer
     */
    public Doctor getDoctor(int id) throws Exception {
        return this.doctorDao.get(id);
    }

    public void updateDoctor(Doctor doctor) throws Exception {
        doctorDao.update(doctor);
    }

}
