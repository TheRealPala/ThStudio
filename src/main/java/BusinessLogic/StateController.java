package BusinessLogic;

import domainModel.State.*;
import domainModel.Customer;
import domainModel.MedicalExam;
import dao.MedicalExamDao;

import java.util.List;
import java.util.Objects;

public class StateController {
    private final MedicalExamController medicalExamController;
    private final CustomerController customerController;
    private final MedicalExamDao medicalExamDao;

    public StateController(MedicalExamController medicalExamController, CustomerController customerController, MedicalExamDao medicalExamDao) {
        this.medicalExamController = medicalExamController;
        this.customerController = customerController;
        this.medicalExamDao = medicalExamDao;
    }

    public void bookMedicalExam(int customerId, int ExamId) throws Exception{
        MedicalExam medicalExam = medicalExamController.getExam(ExamId);
        Customer customer = customerController.getPerson(customerId);
        if (customer == null) throw new IllegalArgumentException("The given customer does not exist.");
        if (medicalExam == null) throw new IllegalArgumentException("The given medical exam does not exist.");

        if (Objects.equals(medicalExam.getStateExtraInfo(), customerId)){
            throw new RuntimeException("The given student is already booked for this exem");
        }

        if (!Objects.equals(medicalExam.getState(), "Available")){
            throw new RuntimeException("You cannot book this exam");
        }
    }

}


