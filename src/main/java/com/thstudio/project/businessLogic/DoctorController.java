package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;

import java.util.List;

public class DoctorController extends AuthorizedController {

    private final DoctorDao doctorDao;
    public DoctorController(DoctorDao doctorDao, PersonDao personDao) throws Exception {
        super(personDao);
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
    public Doctor addDoctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance, String email, String password, String token) throws Exception {
        this.validateToken(token);
        Doctor d = new Doctor(name, surname, dateOfBirth, medicalLicenseNumber, balance, email, password);
        doctorDao.insert(d);
        return d;
    }

    /**
     * Add a new doctor from obj
     *
     * @param doctor The doctor object
     * @return The added doctor
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Doctor addDoctor(Doctor doctor, String token) throws Exception {
        return this.addDoctor(doctor.getName(), doctor.getSurname(), doctor.getDateOfBirth(), doctor.getMedicalLicenseNumber(), doctor.getBalance(), doctor.getEmail(), doctor.getPassword(), token);
    }

    /**
     * get the doctor
     *
     * @param id The id of the doctor
     * @return The customer
     */
    public Doctor getDoctor(int id, String token) throws Exception {
        this.validateToken(token);
        return doctorDao.get(id);
    }

    public void updateDoctor(Doctor doctor, String token) throws Exception {
        this.validateToken(token);
        doctorDao.update(doctor);
    }
    public boolean deleteDoctor(int id, String token) throws Exception {
        this.validateToken(token);
        return doctorDao.delete(id);
    }

    public List<Doctor> getAllPersons(String token) throws Exception {
        this.validateToken(token);
        return doctorDao.getAll();
    }
}
