package businessLogic;

import dao.DoctorDao;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.State.Booked;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorController extends PersonController<Doctor>{
    private DoctorDao doctorDAO;
    private MedicalExamController mec;
    public DoctorController(DoctorDao doctorDAO, MedicalExamController mec) {
        super(doctorDAO);
        this.mec = mec;
    }

    /**
     * Add a new doctor
     * @param name                          The name of the doctor
     * @param surname                       The surname of the doctor
     * @param dateOfBirth                   The date of birth of the doctor
     * @param iban                          The IBAN of the doctor
     * @param medicalLicenseNumber          The medical license number of the doctor
     *
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, String medicalLicenseNumber) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, iban, medicalLicenseNumber);
        return super.addPerson(d);
    }
    //TODO: add method to modify date or other properties of the medical exam, and that notify the customer
    //TODO: add method to get the list of the medical exams
    //TODO: add method to delete the medical exam, and that notify the customer, and define a policy for the refund
    //TODO: add a method to create a new medical exam
    /** modify the Medical exam start time
     * @param me the medical exam
     * @param t the new start time
     * @param id the id of the doctor
     * @return true if the modification was successful, false otherwise
     * @throws Exception
     */

    public boolean modifyMedicalExamStartTime(MedicalExam me, LocalDateTime t, int id) throws Exception {

        if( me.getStartTime().isBefore(LocalDateTime.now()) &&me.getIdDoctor()==id&& me.getEndTime().isAfter(t)) {
            if(me.getState()instanceof Booked){
                //notify the customer
            }
            mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                    me.getIdDoctor(), me.getEndTime(), t, me.getDescription(), me.getTitle(), me.getPrice());

            return true;
        }
        else
            return false;
    }
}
