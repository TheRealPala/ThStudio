package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.Search.Search;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.State.Deleted;
import com.thstudio.project.domainModel.State.State;
import com.thstudio.project.security.Authz;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final NotificationDao notificationDao;
    private final CustomerDao customerDao;
    private final DoctorDao doctorDao;
    private final Authz authz;

    public MedicalExamController(MedicalExamDao medicalExamDao, NotificationDao notificationDao, DoctorDao doctorDao, CustomerDao customerDao) throws Exception{
        this.medicalExamDao = medicalExamDao;
        this.doctorDao = doctorDao;
        this.notificationDao = notificationDao;
        this.customerDao = customerDao;
        this.authz = new Authz();
    }

    private void checkDateTimeBounds(List<MedicalExam> medicalExams, LocalDateTime startTime,
                                     LocalDateTime endTime) throws IllegalArgumentException {
        for (MedicalExam exam : medicalExams) {
            if ((startTime.isAfter(exam.getStartTime()) && startTime.isBefore(exam.getEndTime())) || (endTime.isAfter(exam.getStartTime()) && endTime.isBefore(exam.getEndTime()))) {
                throw new IllegalArgumentException("The given doctor is already occupied in the given time range");
            }
        }
    }

    /**
     * Updates a medical exam
     *
     * @param examId      The id of the medical exam
     * @param endTime     The end time of the medical exam
     * @param startTime   The start time of the medical exam
     * @param description The description of the medical exam
     * @param title       The title of the medical exam
     * @param price       The price of the medical exam
     * @param state       The param of the medical exam
     * @throws Exception If the medical exam is not found, bubbles up exceptions to MedicalExamDAO::update()
     */

    private void updateMedicalExamLogic(int examId, LocalDateTime startTime, LocalDateTime endTime, String description,
                                        String title, double price, State state) throws Exception {
        MedicalExam medicalExam = this.medicalExamDao.get(examId);
        if (medicalExam.getPrice() == price && medicalExam.getStartTime() == startTime && medicalExam.getEndTime() == endTime && medicalExam.getDescription().equals(description) && medicalExam.getTitle().equals(title)) {
            System.out.println("nothing to update");
        } else {

            StringBuilder s = new StringBuilder("esame modificato: \n");

            if (!medicalExam.getStartTime().isEqual(startTime) || !medicalExam.getEndTime().isEqual(endTime)) {

                checkDateTimeBounds(medicalExamDao.getDoctorExams(medicalExam.getIdDoctor()), startTime, endTime);

                if (medicalExam.getStartTime() != startTime) {
                    if (medicalExam.getStartTime().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("exam already started");
                    }
                    s.append("the start time has been changed\n");
                    medicalExam.setStartTime(startTime);
                }

                if (medicalExam.getEndTime() != endTime) {
                    if (medicalExam.getEndTime().isBefore(LocalDateTime.now()) && endTime.isBefore(startTime)) {
                        throw new RuntimeException("illegal update");
                    }
                    s.append("the end time has been changed\n");
                    medicalExam.setEndTime(endTime);
                }
            }

            if (medicalExam.getPrice() != price) {
                if (price <= 0) {
                    throw new RuntimeException("illegal price");
                }
                if (medicalExam.getState() instanceof Booked) {
                    throw new RuntimeException("exam already booked");
                }
                s.append("the price has been changed\n");
                medicalExam.setPrice(price);
            }

            if (!state.equals(medicalExam.getState())) {
                medicalExam.setState(state);
                s.append("the state has been changed\n");
            }

            if (!Objects.equals(medicalExam.getDescription(), description)) {
                s.append("the description has been changed\n");
                medicalExam.setDescription(description);
            }

            if (!Objects.equals(medicalExam.getTitle(), title)) {
                medicalExam.setTitle(title);
                s.append("the title has been changed\n");
            }

            if (medicalExam.getState() instanceof Booked) {
                if (medicalExam.getIdCustomer() == 0) {
                    throw new RuntimeException("Inconsistency in the database");
                }
                Notification nd = new Notification("esame modificato:" + s, medicalExam.getIdCustomer());
                notificationDao.insert(nd);
            }
            this.medicalExamDao.update(medicalExam);
        }
    }

    private void addMedicalExamLogic(MedicalExam medicalExam, Doctor doctor) throws Exception {
        List<MedicalExam> medicalExams = new ArrayList<>();
        try {
            medicalExams = medicalExamDao.getDoctorExams(doctor.getId());
        } catch (RuntimeException e) {
            /* System.out.println("The doctor has not exam, therefore is free in the time specified");*/
        }
        if (!medicalExams.isEmpty()) {
            checkDateTimeBounds(medicalExams, medicalExam.getStartTime(), medicalExam.getEndTime());
        }
        medicalExam.setState(new Available());
        medicalExamDao.insert(medicalExam);
    }

    public MedicalExam addMedicalExam(int idDoctor, int idCustomer, String title, String description,
                                      String startTime, String endTime, double price, String token) throws Exception {

        authz.requireAnyRole(token, "doctor", "admin");
        Doctor doctor = doctorDao.get(idDoctor);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), startTime, endTime, description, title, price);
        medicalExam.setIdCustomer(idCustomer);
        this.addMedicalExamLogic(medicalExam, doctor);
        return medicalExam;
    }

    public MedicalExam addMedicalExam(MedicalExam medicalExam, String token) throws Exception {

        authz.requireAnyRole(token, "doctor", "admin");
        Doctor doctor = doctorDao.get(medicalExam.getIdDoctor());
        this.addMedicalExamLogic(medicalExam, doctor);
        return medicalExam;
    }

    public boolean updateMedicalExam(MedicalExam medicalExam, String token) throws Exception {

        authz.requireAnyRole(token, "doctor", "admin");
        MedicalExam me = medicalExamDao.get(medicalExam.getId());

        if (!me.getStartTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Forbidden! Can't update an exam already started");
        }
        this.updateMedicalExamLogic(medicalExam.getId(), medicalExam.getStartTime(), medicalExam.getEndTime(), medicalExam.getDescription(), medicalExam.getTitle(),
                medicalExam.getPrice(), medicalExam.getState());
        return true;
    }

    /**
     * cancel a medical exam
     *
     * @param medicalExamId The medical exam id
     * @param doctorId      The doctor id
     * @return true, if the medical exam is canceled, raise up RuntimeExceptions otherwise
     */
    public boolean deleteMedicalExam(int medicalExamId, int doctorId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "admin");
        Doctor d = this.doctorDao.get(doctorId);
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        if (me.getIdDoctor() != d.getId()) {
            throw new RuntimeException("Unauthorized request");
        }
        if (LocalDateTime.now().isAfter(me.getStartTime())) {
            throw new RuntimeException("Can't cancel an exam already started");
        }

        if (me.getState() instanceof Booked) {
            double medicalExamPrice = me.getPrice();
            Customer c = this.customerDao.get(me.getIdCustomer());
            d.setBalance(d.getBalance() - medicalExamPrice);
            c.setBalance(c.getBalance() + medicalExamPrice);
            this.doctorDao.update(d);
            this.customerDao.update(c);
            Notification nd = new Notification("Deleted exam " + me.getTitle() + " by:" + d.getName(), c.getId());
            notificationDao.insert(nd);
        }

        me.setState(new Deleted(LocalDateTime.now()));
        this.medicalExamDao.deleteBookedMedicalExam(me);
        return true;
    }

    /**
     * Return the given medical exam
     *
     * @param examId The id of the medical exam
     * @return The medical exam
     * @throws Exception If the medical exam is not found
     */
    public MedicalExam getExam(int examId, String token) throws Exception {

        authz.requireAnyRole(token, "doctor", "admin", "customer");
        return medicalExamDao.get(examId);
    }

    /**
     * Return a read-only list of medical exam
     *
     * @return The list of medical exam
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> getAll(String token) throws Exception {

        authz.requireAnyRole(token, "doctor", "admin", "customer");
        return unmodifiableList(this.medicalExamDao.getAll());
    }

    /**
     * Return a read-only list of medical exam for the given doctor
     *
     * @param idDoctor The id of the doctor
     * @return The list of medical exam
     * @throws Exception If the doctor is not found, or if the doctor doesn't have any medical exam
     */
    public List<MedicalExam> getDoctorExams(int idDoctor, String token) throws Exception {
        authz.requireAnyRole(token, "doctor", "admin");
        return this.medicalExamDao.getDoctorExams(idDoctor);

    }

    /**
     * Return a read-only list of medical exam for the given customer
     *
     * @param idCustomer The id of the customer
     * @return The list of medical exam
     * @throws Exception If the customer is not found, or if the customer doesn't have any medical exam
     */
    public List<MedicalExam> getCustomerExams(int idCustomer, String token) throws Exception {
        authz.requireAnyRole(token, "customer", "admin", "doctor");
        return unmodifiableList(this.medicalExamDao.getCustomerExams(idCustomer));
    }

    /**
     * Return a read-only list of medical exam for the given state
     *
     * @param state The state of the medical exam
     * @return The list of medical exam
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> getExamsByState(State state, String token) throws Exception {
        authz.requireAnyRole(token, "doctor", "admin", "customer");
        return unmodifiableList(this.medicalExamDao.getExamsByState(state.getState()));
    }

    /**
     * Search for medical exams
     *
     * @param search The search object
     * @return The list of medical exams
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> search(Search search, String token) throws Exception {
        authz.requireAnyRole(token, "doctor", "admin", "customer");
        return unmodifiableList(this.medicalExamDao.search(search));
    }

}
