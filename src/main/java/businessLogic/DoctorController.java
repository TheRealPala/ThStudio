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
    /** modify the Medical exam end time
     * @param me the medical exam
     * @param t the new end time
     * @param id the id of the doctor
     * @return true if the modification was successful, false otherwise
     * @throws Exception
     */
    public boolean modifyMedicalExamEndTime(MedicalExam me, LocalDateTime t, int id) throws Exception {
        if( me.getEndTime().isBefore(LocalDateTime.now()) &&me.getIdDoctor()==id&&me.getStartTime().isBefore(t)) {
            mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                    me.getIdDoctor(), t, me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());

            return true;
        }
        else
            return false;
    }
    /** modify the Medical exam description
     * @param me the medical exam
     * @param s the new description
     * @param id the id of the doctor
     * @return true if the modification was successful, false otherwise
     * @throws Exception
     */
    public boolean modifyMedicalExamDescription(MedicalExam me, String s, int id) throws Exception {
        if(me.getIdDoctor()==id&&me.getStartTime().isBefore(LocalDateTime.now())) {
            if(me.getState()instanceof Booked){
                //notify the customer
            }
            mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                    me.getIdDoctor(), me.getEndTime(), me.getStartTime(), s, me.getTitle(), me.getPrice());

            return true;
        }
        else
            return false;
    }
    /** modify the Medical exam title
     * @param me the medical exam
     * @param s the new title
     * @param id the id of the doctor
     * @return true if the modification was successful, false otherwise
     * @throws Exception
     */
    public boolean modifyMedicalExamTitle(MedicalExam me, String s, int id) throws Exception {
        if(me.getIdDoctor()==id&&me.getStartTime().isBefore(LocalDateTime.now())) {
            if(me.getState()instanceof Booked){
                //notify the customer
            }
            mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                    me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), s, me.getPrice());

            return true;
        }
        else
            return false;
    }
    /** modify the Medical exam price
     * @param me the medical exam
     * @param d the new price
     * @param id the id of the doctor
     * @return true if the modification was successful, false otherwise
     * @throws Exception
     */

    public boolean modifyMedicalExamPrice(MedicalExam me, double d, int id) throws Exception {
        if(me.getIdDoctor()==id&&me.getStartTime().isBefore(LocalDateTime.now())) {
            if(me.getState().equals("Available")) {
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), d);
                return true;
            }
            else{
                System.out.println("The medical exam is already booked");
                return false;
            }
        }
        else
            return false;
    }
    /** delete the Medical exam
     * @param me the medical exam
     * @param id the id of the doctor
     * @return true if the deletion was successful, false otherwise
     * @throws Exception
     */
    public boolean deleteMedicalExam(MedicalExam me, int id) throws Exception {
        if(me.getIdDoctor()==id&&me.getStartTime().isBefore(LocalDateTime.now())) {
            if(me.getState().equals("Booked")){
                mec.refund(me.getIdCustomer(), me);
            }
            mec.removeMedicalExam(me.getId());
            return true;
        }
        else
            return false;
    }
}
