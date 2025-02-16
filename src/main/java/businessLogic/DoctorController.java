package businessLogic;

import dao.DoctorDao;
import dao.MariaDbDocumentDao;
import dao.MariaDbNotificationDao;
import domainModel.Doctor;
import domainModel.Document;
import domainModel.MedicalExam;
import domainModel.Notification;
import domainModel.State.Available;
import domainModel.State.Booked;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorController extends PersonController<Doctor>{
    private DoctorDao doctorDAO;
    private MedicalExamController mec;
    private MariaDbNotificationDao notification;
    private MariaDbDocumentDao documentDao;
    public DoctorController(DoctorDao doctorDAO, MedicalExamController mec, MariaDbNotificationDao nd, MariaDbDocumentDao dd) {
        super(doctorDAO);
        this.mec = mec;
        this.doctorDAO = doctorDAO;
        notification = nd;
        documentDao = dd;

    }

    /**
     * Add a new doctor
     * @param name                          The name of the doctor
     * @param surname                       The surname of the doctor
     * @param dateOfBirth                   The date of birth of the doctor
     * @param medicalLicenseNumber          The medical license number of the doctor
     * @param balance                       The balance of the doctor
     *
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, medicalLicenseNumber, balance);
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
                String s = "The start time of the medical exam has been modified to "+t;
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice(),s);

            } else {
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), me.getEndTime(), t, me.getDescription(), me.getTitle(), me.getPrice());
            }

            return true;
        }
        else
            System.out.println("no modification ");
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
            if(me.getState()instanceof Booked){
                String s = "The end time of the medical exam has been modified to "+t;
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), t, me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice(),s);
            } else {
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), t, me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());
            }

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
                String s1 = "The description of the medical exam has been modified to "+s;
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), me.getEndTime(), me.getStartTime(), s, me.getTitle(), me.getPrice(),s1);
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
                String s1 = "The title of the medical exam has been modified to "+s;
                mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                        me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), s, me.getPrice(),s1);
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
            if(me.getState()instanceof Available) {
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
                if(me.getStartTime().isBefore(LocalDateTime.now())) {
                    String s = "The medical exam has been canceled";
                    mec.updateMedicalExam(me.getId(), me.getIdCustomer(),
                            me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice(), s);
                    mec.refund(me.getIdCustomer(), me);
                }
            }
            mec.removeMedicalExam(me.getId());
            return true;
        }
        else
            return false;
    }
    /** create a new Medical exam
     * @param idDoctor the id of the doctor
     * @param endTime the end time of the medical exam
     * @param startTime the start time of the medical exam
     * @param description the description of the medical exam
     * @param title the title of the medical exam
     * @param price the price of the medical exam
     * @param tags the tags of the medical exam
     * @throws Exception
     */
    public void createMedicalExam(int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, ArrayList<Tag> tags) throws Exception {
        mec.addMedicalExam(idDoctor, endTime, startTime, description, title, price, tags);
    }
    /** get the Medical exams of a doctor
     * @param id the id of the doctor
     * @return the list of medical exams
     * @throws Exception
     */
    public List<MedicalExam> getMedicalExam(int id) throws Exception {
        return mec.getDoctorExams(id);
    }
    public List<Notification> getNotifications(int id) throws Exception {
        return notification.getNotificationsByReceiverId(id);
    }
    public List<Document> getDocuments(int id) throws Exception {
        return documentDao.getByOwner(id);
    }
    private void addDocument(Document document) throws Exception {
        documentDao.insert(document);
    }
    public void addDocumentToMedicalExam(String title, String path, int ownerId, int receiverId, MedicalExam me) throws Exception {
        Document document = new Document(title, path, ownerId);
        if(me.getState() instanceof Booked) {
            document.setReceiverId(receiverId); // or me.getIdCustomer()
            Notification nd = new Notification("New document "+title+" by :"+ownerId,receiverId);
            notification.insert(nd);
        }
        document.setMedicalExamId(me.getId());
        addDocument(document);
    }
    public void addDocumentToCustomer(String title, String path, int ownerId, int receiverId) throws Exception {
        Document document = new Document(title, path, ownerId);
        document.setReceiverId(receiverId);
        addDocument(document);
        Notification nd = new Notification("New document "+title+" by :"+ownerId,receiverId);
        notification.insert(nd);
    }
    public void update(Doctor doctor) throws Exception {
        doctorDAO.update(doctor);
    }



}
