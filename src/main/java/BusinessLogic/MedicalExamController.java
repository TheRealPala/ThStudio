package BusinessLogic;

import dao.MedicalExamDao;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.State.State;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;


public class MedicalExamController {
    private final MedicalExamDao medicalExamDao;
    private final PersonController<Doctor> doctorController;

    public MedicalExamController(MedicalExamDao medicalExamDao, PersonController<Doctor> doctorController) {
        this.medicalExamDao = medicalExamDao;
        this.doctorController = doctorController;
    }

    //TODO aggiungere Doc block

    public int addMedicalExam(int idCustomer, String stateExtraInfo, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, State state, String description, String title, double price) throws Exception {
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

        MedicalExam medicalExam = new MedicalExam(idCustomer, stateExtraInfo, idDoctor, endTime, startTime, state, description, title, price);
        //TODO aggiungete tag
        medicalExamDao.insert(medicalExam);
        return medicalExam.getId();
    }

    public void updateMedicalExam(int ExamId, int idCustomer, String stateExtraInfo, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, State state, String description, String title, double price) throws Exception {
        if (this.medicalExamDao.get(ExamId) == null)
            throw new IllegalArgumentException("Medical Exam not found");

        MedicalExam medicalExam = new MedicalExam(idCustomer, stateExtraInfo, idDoctor, endTime, startTime, state, description, title, price);
        this.medicalExamDao.update(medicalExam);
    }

    public boolean removeMedicalExam(int id) throws Exception {
        return medicalExamDao.delete(id);
    }

    public MedicalExam getExam (int id) throws Exception {
        return medicalExamDao.get(id);
    }

    public List<MedicalExam> getAll() throws Exception {
        return unmodifiableList(this.medicalExamDao.getAll());
    }

    public List<MedicalExam> getDoctorExams(int idDoctor) throws Exception {
        return unmodifiableList(this.medicalExamDao.getDoctorExams(idDoctor));
    }

    public List<MedicalExam> getCustomerExams(int idCustomer) throws Exception {
        return unmodifiableList(this.medicalExamDao.getCustomerExams(idCustomer));
    }

}
