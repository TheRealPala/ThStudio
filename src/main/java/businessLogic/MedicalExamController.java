package businessLogic;

import dao.*;
import domainModel.Customer;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.Notification;
import domainModel.Search.Search;
import domainModel.State.Booked;
import domainModel.State.State;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final PersonController<Doctor> doctorController;
    private CustomerController c;
    NotificationDao notification;
    CustomerDao customerDao;

    public MedicalExamController(MedicalExamDao medicalExamDao, DoctorDao d,
                                 CustomerController c, NotificationDao nd, DocumentDao dd, CustomerDao customerDao) {
        this.medicalExamDao = medicalExamDao;
        this.doctorController = new DoctorController(d, this, nd, dd, customerDao);
        this.c = c;
        notification = nd;
        this.customerDao = customerDao;

    }

    private void checkDateTimeBounds(List<MedicalExam> medicalExams, LocalDateTime startTime,
                                     LocalDateTime endTime) throws IllegalArgumentException {
        for (MedicalExam exam : medicalExams) {
            if ((exam.getStartTime().isBefore(endTime) || exam.getStartTime().equals(endTime))
                    && (exam.getEndTime().isAfter(startTime) || exam.getEndTime().equals(startTime)))
                throw new IllegalArgumentException("The given doctor is already occupied in the given time range (in course #" + exam.getId() + ")");
        }
    }

    public int addMedicalExam(int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description,
                              String title, double price) throws Exception {
        Doctor doctor = doctorController.getPerson(idDoctor);
        if (doctor == null)
            throw new IllegalArgumentException("The specified doctor was not found");

        // Check if the given doctor is not already occupied for the given time range
        checkDateTimeBounds(medicalExamDao.getDoctorExams(idDoctor), startTime, endTime);
        MedicalExam medicalExam = new MedicalExam(idDoctor, startTime, endTime, description, title, price);
        medicalExamDao.insert(medicalExam);
        return medicalExam.getId();
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

    public void updateMedicalExam(int examId, LocalDateTime endTime, LocalDateTime startTime, String description,
                                  String title, double price, ArrayList<Tag> tags, State state) throws Exception {
        MedicalExam medicalExam = this.medicalExamDao.get(examId);
        if (medicalExam.getPrice() == price && medicalExam.getStartTime() == startTime && medicalExam.getEndTime() == endTime && medicalExam.getDescription().equals(description) && medicalExam.getTitle().equals(title) && medicalExam.getTags().equals(tags)) {
            System.out.println("nothing to update");
        } else {
            StringBuilder s = new StringBuilder("esame modificato: \n");
            if (medicalExam.getStartTime().isEqual(startTime) || medicalExam.getEndTime().isEqual(endTime)) {
                boolean isDateBoundValid = true;

                if (medicalExam.getStartTime() != startTime) {
                    if (medicalExam.getStartTime().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("exam already started");
                    }
                    s.append("the start time has been changed\n");
                }
                if (medicalExam.getEndTime() != endTime) {
                    if (medicalExam.getEndTime().isBefore(LocalDateTime.now()) && endTime.isBefore(startTime)) {
                        throw new RuntimeException("illegal update");
                    }

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
            }
            if (!state.equals(medicalExam.getState())) {
                medicalExam.setState(state);
                s.append("the state has been changed\n");
            }
            if (!Objects.equals(medicalExam.getDescription(), description)) {
                s.append("the description has been changed\n");
            }
            if (!Objects.equals(medicalExam.getTitle(), title)) {
                s.append("the title has been changed\n");
            }
            if (medicalExam.getTags() != tags) {
                s.append("the tags have been changed\n");
            }
            medicalExam.setTags(tags);
            medicalExam.setPrice(price);
            medicalExam.setStartTime(startTime);
            medicalExam.setEndTime(endTime);
            medicalExam.setDescription(description);
            medicalExam.setTitle(title);

            if (medicalExam.getState() instanceof Booked) {
                Notification nd = new Notification("esame modificato:" + s, medicalExam.getIdCustomer());  // o array di notifiche con ogni cambiamento o singola stringa con tutti i cambiamenti
                notification.insert(nd);
            }
            this.medicalExamDao.update(medicalExam);
        }
    }

    /**
     * Delete a medical exam
     *
     * @param ExamId The id of the medical exam
     * @return True if the medical exam is deleted, false otherwise
     * @throws Exception If the medical exam is not found, bubbles up exceptions to MedicalExamDAO::delete()
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

    //attach and detach tags and make a revision of single controllers responsabilities
}
