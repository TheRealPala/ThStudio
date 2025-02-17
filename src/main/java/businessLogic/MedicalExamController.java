package businessLogic;

import dao.*;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.Notification;
import domainModel.Search.Search;
import domainModel.State.State;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final PersonController<Doctor> doctorController;
    private CustomerController c;
    NotificationDao notification;

    public MedicalExamController(MedicalExamDao medicalExamDao, DoctorDao d,
                                 CustomerController c, NotificationDao nd, DocumentDao dd) {
        this.medicalExamDao = medicalExamDao;
        this.doctorController = new DoctorController(d, this, nd, dd);
        this.c = c;
        notification = nd;

    }

    /**
     * Adds a new medical exam to the list
     *
     * @param idCustomer  The id of the customer
     * @param idDoctor    The id of the doctor
     * @param endTime     The end time of the medical exam
     * @param startTime   The start time of the medical exam
     * @param description The description of the medical exam
     * @param title       The title of the medical exam
     * @param price       The price of the medical exam
     * @param tags        The tags of the medical exam
     * @return The id of the newly created medical exam
     * @throws Exception                Bubbles up exceptions of MedicalExamDAO::insert()
     * @throws IllegalArgumentException If the doctor is already occupied in the given time range or if the doctor is not found
     */
    public int addMedicalExam(int idCustomer, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, ArrayList<Tag> tags) throws Exception {
        Doctor doctor = doctorController.getPerson(idDoctor);
        if (doctor == null)
            throw new IllegalArgumentException("The specified doctor was not found");

        // Check if the given doctor is not already occupied for the given time range
        for (MedicalExam exam : medicalExamDao.getDoctorExams(idDoctor)) {
            if ((exam.getStartTime().isBefore(endTime) || exam.getStartTime().equals(endTime))
                    && (exam.getEndTime().isAfter(startTime) || exam.getEndTime().equals(startTime)))
                throw new IllegalArgumentException("The given doctor is already occupied in the given time range (in course #" + exam.getId() + ")");
        }

        MedicalExam medicalExam = new MedicalExam(idCustomer, idDoctor, endTime, startTime, description, title, price);
        medicalExam.setTags(tags);

        medicalExamDao.insert(medicalExam);
        return medicalExam.getId();
    }

    public int addMedicalExam(int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, ArrayList<Tag> tags) throws Exception {
        Doctor doctor = doctorController.getPerson(idDoctor);
        if (doctor == null)
            throw new IllegalArgumentException("The specified doctor was not found");

        // Check if the given doctor is not already occupied for the given time range
        for (MedicalExam exam : medicalExamDao.getDoctorExams(idDoctor)) {
            if ((exam.getStartTime().isBefore(endTime) || exam.getStartTime().equals(endTime))
                    && (exam.getEndTime().isAfter(startTime) || exam.getEndTime().equals(startTime)))
                throw new IllegalArgumentException("The given doctor is already occupied in the given time range (in course #" + exam.getId() + ")");
        }

        MedicalExam medicalExam = new MedicalExam(idDoctor, endTime, startTime, description, title, price, tags);
        medicalExamDao.insert(medicalExam);
        return medicalExam.getId();
    }

    /**
     * Updates a medical exam
     *
     * @param ExamId      The id of the medical exam
     * @param idCustomer  The id of the customer
     * @param idDoctor    The id of the doctor
     * @param endTime     The end time of the medical exam
     * @param startTime   The start time of the medical exam
     * @param description The description of the medical exam
     * @param title       The title of the medical exam
     * @param price       The price of the medical exam
     * @throws Exception If the medical exam is not found, bubbles up exceptions of MedicalExamDAO::update()
     */
    public void updateMedicalExam(int ExamId, int idCustomer, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price) throws Exception {
        if (this.medicalExamDao.get(ExamId) == null)
            throw new IllegalArgumentException("Medical Exam not found");

        MedicalExam medicalExam = new MedicalExam(idCustomer, idDoctor, endTime, startTime, description, title, price);
        this.medicalExamDao.update(medicalExam);
    }

    public void updateMedicalExam(int ExamId, int idCustomer, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, String s) throws Exception {
        if (this.medicalExamDao.get(ExamId) == null)
            throw new IllegalArgumentException("Medical Exam not found");

        MedicalExam medicalExam = new MedicalExam(idCustomer, idDoctor, endTime, startTime, description, title, price);
        this.medicalExamDao.update(medicalExam);
        Notification nd = new Notification("esame modificato:" + s, idCustomer);
        notification.insert(nd);

    }

    /**
     * Delete a medical exam
     *
     * @param ExamId The id of the medical exam
     * @return True if the medical exam is deleted, false otherwise
     * @throws Exception If the medical exam is not found, bubbles up exceptions of MedicalExamDAO::delete()
     */
    public boolean removeMedicalExam(int ExamId) throws Exception {
        return medicalExamDao.delete(ExamId);

    }

    /**
     * Return the given medical exam
     *
     * @param ExamId The id of the medical exam
     * @return The medical exam
     * @throws Exception If the medical exam is not found
     */
    public MedicalExam getExam(int ExamId) throws Exception {
        return medicalExamDao.get(ExamId);
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
     * @throws Exception If the doctor is not found or if the doctor doesn't have any medical exam
     */
    public List<MedicalExam> getDoctorExams(int idDoctor) throws Exception {
        return this.medicalExamDao.getDoctorExams(idDoctor);

    }

    /**
     * Return a read-only list of medical exam for the given customer
     *
     * @param idCustomer The id of the customer
     * @return The list of medical exam
     * @throws Exception If the customer is not found or if the customer doesn't have any medical exam
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

    public void refund(int idCustomer, MedicalExam me) throws Exception {
        if (idCustomer == me.getIdCustomer()) {
            c.getCustomer(idCustomer).setBalance(c.getCustomer(idCustomer).getBalance() + me.getPrice());
        } else {
            throw new RuntimeException("not your exam");
        }
    }


}
