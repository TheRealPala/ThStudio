package businessLogic;

import domainModel.State.*;
import domainModel.Customer;
import domainModel.MedicalExam;
import dao.MedicalExamDao;

import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;

public class StateController {
    private final MedicalExamController medicalExamController;
    private final CustomerController customerController;
    private final MedicalExamDao medicalExamDao;

    public StateController(MedicalExamController medicalExamController, CustomerController customerController, MedicalExamDao medicalExamDao) {
        this.medicalExamController = medicalExamController;
        this.customerController = customerController;
        this.medicalExamDao = medicalExamDao;
    }

     /**
      * Book a medical exam for a customer
      *
      * @param customerId        The id of the customer
      * @param ExamId            The id of the medical exam
      * @throws                  Exception If the customer or the medical exam is not found
      */
     public void bookMedicalExam(int customerId, int ExamId) throws Exception{
        MedicalExam medicalExam = medicalExamController.getExam(ExamId);
        Customer customer = customerController.getPerson(customerId);
        if (customer == null) throw new IllegalArgumentException("The given customer does not exist.");
        if (medicalExam == null) throw new IllegalArgumentException("The given medical exam does not exist.");

        if (Objects.equals(medicalExam.getStateExtraInfo(), customerId)){
            throw new RuntimeException("The given customer is already booked for this exem");
        }

        if (!Objects.equals(medicalExam.getState(), "Available")){
            throw new RuntimeException("You cannot book this exam");
        }
        
        for (MedicalExam m: this.getCustomerBookedExam(customerId)){
            if ((m.getStartTime().isBefore(medicalExam.getEndTime()) || m.getStartTime().equals(medicalExam.getEndTime()))
                    && (m.getEndTime().isAfter(medicalExam.getStartTime()) || m.getEndTime().equals(medicalExam.getStartTime())))
                throw new RuntimeException("The given customer is already occupied in the given time range (in course #" + m.getId() + ")");
        }

        Booked book = new Booked();

        this.medicalExamDao.changeState(ExamId, book);
    }

    public void cancelMedicalExamBooking(int customerId, int ExamId) throws Exception{
        MedicalExam medicalExam = medicalExamController.getExam(ExamId);
        Customer customer = customerController.getPerson(customerId);
        if (customer == null) throw new IllegalArgumentException("The given customer does not exist.");
        if (medicalExam == null) throw new IllegalArgumentException("The given medical exam does not exist.");

        if (!Objects.equals(medicalExam.getState(), "Booked")){
            throw new RuntimeException("You cannot cancel this exam because it is not booked");
        }

        if (!Objects.equals(medicalExam.getStateExtraInfo(), customerId)){
            throw new RuntimeException("The given customer is not booked for this exam");
        }

        this.medicalExamDao.changeState(ExamId, new Available());
    }

    /**
     * Cancel a medical exam
     *
     * @param ExamId            The id of the medical exam
     * @throws Exception        If the medical exam is not found or if it is already completed
     */
    public void deleteMedicalExam(int ExamId) throws Exception{
        MedicalExam medicalExam = medicalExamController.getExam(ExamId);
        if (medicalExam == null) throw new IllegalArgumentException("The given medical exam does not exist.");

        if (Objects.equals(medicalExam.getState(), "Completed")){
            throw new RuntimeException("You cannot cancel a exam that is already completed");
        }

        LocalDateTime ldt = LocalDateTime.now();
        this.medicalExamDao.changeState(ExamId, new Deleted(ldt));
    }

    /**
     * Complete a medical exam
     *
     * @param ExamId            The id of the medical exam
     * @throws Exception        If the medical exam is not found or if it is already completed
     */
    public void completeMedicalExam(int ExamId) throws Exception{
        MedicalExam medicalExam = medicalExamController.getExam(ExamId);
        if (medicalExam == null) throw new IllegalArgumentException("The given medical exam does not exist.");

        if (Objects.equals(medicalExam.getState(), "Completed")){
            throw new RuntimeException("The exam is already completed");
        }

        LocalDateTime ldt = LocalDateTime.now();
        this.medicalExamDao.changeState(ExamId, new Completed(ldt));
    }

    /**
     * Get all the exam booked by a customer.
     *
     * @param customerId        The id of the customer
     * @throws Exception        If the customer is not found or if doesn't have any booked exam
     */
    private List<MedicalExam> getCustomerBookedExam(int customerId) throws Exception {
        return medicalExamDao.getCustomerBookedExams(customerId);
    }

}


