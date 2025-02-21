package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.Search.Search;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.State.State;
import com.thstudio.project.domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final NotificationDao notificationDao;
    private final DoctorDao doctorDao;

    public MedicalExamController(MedicalExamDao medicalExamDao, NotificationDao notificationDao, DoctorDao doctorDao) {
        this.medicalExamDao = medicalExamDao;
        this.doctorDao = doctorDao;
        this.notificationDao = notificationDao;
    }

    private void checkDateTimeBounds(List<MedicalExam> medicalExams, LocalDateTime startTime,
                                     LocalDateTime endTime) throws IllegalArgumentException {
        boolean outcome = true;
        for (MedicalExam exam : medicalExams) {
            if ((exam.getStartTime().isBefore(endTime) || exam.getStartTime().equals(endTime))
                    && (exam.getEndTime().isAfter(startTime) || exam.getEndTime().equals(startTime)))
                throw new IllegalArgumentException("The given doctor is already occupied in the given time range (in course #" + exam.getId() + ")");
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
     * @param tags        The tags of the medical exam
     * @param state       The param of the medical exam
     * @throws Exception If the medical exam is not found, bubbles up exceptions to MedicalExamDAO::update()
     */

    private void updateLogicForMedicalExam(int examId, LocalDateTime endTime, LocalDateTime startTime, String description,
                                           String title, double price, ArrayList<Tag> tags, State state) throws Exception {
        MedicalExam medicalExam = this.medicalExamDao.get(examId);
        if (medicalExam.getPrice() == price && medicalExam.getStartTime() == startTime && medicalExam.getEndTime() == endTime && medicalExam.getDescription().equals(description) && medicalExam.getTitle().equals(title) && medicalExam.getTags().equals(tags)) {
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

            if (medicalExam.getTags() != tags) {
                s.append("the tags have been changed\n");
                medicalExam.setTags(tags);
            }

            if (medicalExam.getState() instanceof Booked) {
                Notification nd = new Notification("esame modificato:" + s, medicalExam.getIdCustomer());  // o array di notifiche con ogni cambiamento o singola stringa con tutti i cambiamenti
                notificationDao.insert(nd);
            }
            this.medicalExamDao.update(medicalExam);
        }
    }

    public MedicalExam addMedicalExam(int idDoctor, String title, String description, String startTime, String endTime, double price) throws Exception {
        Doctor doctor = doctorDao.get(idDoctor);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(), startTime, endTime, description, title, price);
        try {
            checkDateTimeBounds(medicalExamDao.getDoctorExams(doctor.getId()), medicalExam.getStartTime(), medicalExam.getEndTime());
        } catch (RuntimeException e) {
            System.out.println("The doctor has not exam, therefore is free in the time specified");
        }
        medicalExam.setState(new Available());
        medicalExamDao.insert(medicalExam);
        return medicalExam;
    }

    public boolean updateMedicalExam(int medicalExamId, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, ArrayList<Tag> tags, State state) throws Exception {
        boolean outcome = false;
        MedicalExam me = medicalExamDao.get(medicalExamId);
        if (me.getIdDoctor() == idDoctor && me.getStartTime().isAfter(LocalDateTime.now())) {
            this.updateLogicForMedicalExam(medicalExamId, endTime, startTime, description, title, price, tags, state);
            outcome = true;
        }
        return outcome;
    }

    /**
     * Return the given medical exam
     *
     * @param examId The id of the medical exam
     * @return The medical exam
     * @throws Exception If the medical exam is not found
     */
    public MedicalExam getExam(int examId) throws Exception {
        return medicalExamDao.get(examId);
    }

    /**
     * Return a read-only list of medical exam
     *
     * @return The list of medical exam
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> getAll() throws Exception {
        return unmodifiableList(this.medicalExamDao.getAll());
    }

    /**
     * Return a read-only list of medical exam for the given doctor
     *
     * @param idDoctor The id of the doctor
     * @return The list of medical exam
     * @throws Exception If the doctor is not found, or if the doctor doesn't have any medical exam
     */
    public List<MedicalExam> getDoctorExams(int idDoctor) throws Exception {
        return this.medicalExamDao.getDoctorExams(idDoctor);

    }

    /**
     * Return a read-only list of medical exam for the given customer
     *
     * @param idCustomer The id of the customer
     * @return The list of medical exam
     * @throws Exception If the customer is not found, or if the customer doesn't have any medical exam
     */
    public List<MedicalExam> getCustomerExams(int idCustomer) throws Exception {
        return unmodifiableList(this.medicalExamDao.getCustomerExams(idCustomer));
    }

    /**
     * Return a read-only list of medical exam for the given state
     *
     * @param state The state of the medical exam
     * @return The list of medical exam
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> getExamsByState(State state) throws Exception {
        return unmodifiableList(this.medicalExamDao.getExamsByState(state.toString()));
    }

    /**
     * Search for medical exams
     *
     * @param search The search object
     * @return The list of medical exams
     * @throws Exception If there are no medical exams
     */
    public List<MedicalExam> search(Search search) throws Exception {
        return unmodifiableList(this.medicalExamDao.search(search));
    }

}
