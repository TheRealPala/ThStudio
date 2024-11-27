package BusinessLogic;

import dao.MedicalExamDao;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.Search.Search;
import domainModel.State.State;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final PersonController<Doctor> doctorController;

    public MedicalExamController(MedicalExamDao medicalExamDao, PersonController<Doctor> doctorController) {
        this.medicalExamDao = medicalExamDao;
        this.doctorController = doctorController;
    }

    /**
     * Adds a new medical exam to the list
     *
     * @param idCustomer        The id of the customer
     * @param idDoctor          The id of the doctor
     * @param endTime           The end time of the medical exam
     * @param startTime         The start time of the medical exam
     * @param description       The description of the medical exam
     * @param title             The title of the medical exam
     * @param price             The price of the medical exam
     *
     * @return The id of the newly created medical exam
     *
     * @throws Exception If the doctor is not found, bubbles up exceptions of MedicalExamDAO::insert()
     * @throws IllegalArgumentException If the doctor is already occupied in the given time range
     */
    public int addMedicalExam(int idCustomer, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price) throws Exception {
        Doctor doctor = doctorController.getPerson(idDoctor);
        if (doctor == null)
            throw new IllegalArgumentException("Doctor not found");

        // Check if the given doctor is not already occupied for the given time range
        for (MedicalExam i : this.medicalExamDao.getAll()) {
            if (i.getIdDoctor() == idDoctor) {
                if ((i.getStartTime().isBefore(endTime) || i.getStartTime().equals(endTime))
                        && (i.getEndTime().isAfter(startTime) || i.getEndTime().equals(startTime)))
                    throw new RuntimeException("The given doctor is already occupied in the given time range (in course #" + i.getId() + ")");
            }
        }

        MedicalExam medicalExam = new MedicalExam(idCustomer, idDoctor, endTime, startTime, description, title, price);
        //TODO aggiungete tag
        medicalExamDao.insert(medicalExam);
        return medicalExam.getId();
    }

    /**
     * Updates a medical exam
     *
     * @param ExamId            The id of the medical exam
     * @param idCustomer        The id of the customer
     * @param idDoctor          The id of the doctor
     * @param endTime           The end time of the medical exam
     * @param startTime         The start time of the medical exam
     * @param description       The description of the medical exam
     * @param title             The title of the medical exam
     * @param price             The price of the medical exam
     *
     * @throws Exception If the medical exam is not found, bubbles up exceptions of MedicalExamDAO::update()
     */
    public void updateMedicalExam(int ExamId, int idCustomer, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price) throws Exception {
        if (this.medicalExamDao.get(ExamId) == null)
            throw new IllegalArgumentException("Medical Exam not found");

        MedicalExam medicalExam = new MedicalExam(idCustomer, idDoctor, endTime, startTime, description, title, price);
        this.medicalExamDao.update(medicalExam);
    }

    /**
     * Delete a medical exam
     *
     * @param ExamId            The id of the medical exam
     * @return                  True if the medical exam is deleted, false otherwise
     * @throws Exception        If the medical exam is not found, bubbles up exceptions of MedicalExamDAO::delete()
     */
    public boolean removeMedicalExam(int ExamId) throws Exception {
        return medicalExamDao.delete(ExamId);
    }

    /**
     * Return the given medical exam
     *
     * @param ExamId            The id of the medical exam
     * @return                  The medical exam
     * @throws Exception        If the medical exam is not found
     */
    public MedicalExam getExam (int ExamId) throws Exception {
        return medicalExamDao.get(ExamId);
    }

    /**
     * Return a read-only list of medical exam
     *
     * @return                  The list of medical exam
     * @throws Exception        If there are no medical exams
     */
    public List<MedicalExam> getAll() throws Exception {
        return unmodifiableList(this.medicalExamDao.getAll());
    }

    /**
     * Return a read-only list of medical exam for the given doctor
     *
     * @param idDoctor          The id of the doctor
     * @return                  The list of medical exam
     * @throws Exception        If the doctor is not found or if the doctor doesn't have any medical exam
     */
    public List<MedicalExam> getDoctorExams(int idDoctor) throws Exception {
        return unmodifiableList(this.medicalExamDao.getDoctorExams(idDoctor));
    }

    /**
     * Return a read-only list of medical exam for the given customer
     *
     * @param idCustomer        The id of the customer
     * @return                  The list of medical exam
     * @throws Exception        If the customer is not found or if the customer doesn't have any medical exam
     */
    public List<MedicalExam> getCustomerExams(int idCustomer) throws Exception {
        return unmodifiableList(this.medicalExamDao.getCustomerExams(idCustomer));
    }

    public List<MedicalExam> search(Search search) throws Exception {
      System.out.println(search.getSearchQuery());
      return medicalExamDao.search(search.getSearchQuery());
    }

}
