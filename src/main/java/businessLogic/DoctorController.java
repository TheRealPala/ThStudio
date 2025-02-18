package businessLogic;

import dao.*;
import domainModel.*;
import domainModel.State.Available;
import domainModel.State.Booked;
import domainModel.State.State;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorController extends PersonController<Doctor> {
    private DoctorDao doctorDao;
    private MedicalExamController medicalExamController;
    private NotificationDao notificationDao;
    private DocumentDao documentDao;
    private CustomerDao customerDao;

    public DoctorController(DoctorDao doctorDao, MedicalExamController medicalExamController,
                            NotificationDao notificationDao, DocumentDao documentDao, CustomerDao customerDao ) {
        super(doctorDao);
        this.medicalExamController = medicalExamController;
        this.doctorDao = doctorDao;
        this.notificationDao = notificationDao;
        this.documentDao = documentDao;
        this.customerDao = customerDao;
    }

    /**
     * Add a new doctor
     *
     * @param name                 The name of the doctor
     * @param surname              The surname of the doctor
     * @param dateOfBirth          The date of birth of the doctor
     * @param medicalLicenseNumber The medical license number of the doctor
     * @param balance              The balance of the doctor
     * @return The CF of the newly created doctor
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public String addDoctor(String name, String surname, String dateOfBirth, String medicalLicenseNumber, double balance) throws Exception {
        Doctor d = new Doctor(name, surname, dateOfBirth, medicalLicenseNumber, balance);
        return super.addPerson(d);
    }

    public boolean modifyMedicalExam(int medicalExamId, int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price, ArrayList<Tag> tags, State state) throws Exception {
        boolean outcome = false;
        MedicalExam me = medicalExamController.getExam(medicalExamId);
        if (me.getIdDoctor() == idDoctor && me.getStartTime().isAfter(LocalDateTime.now())) {
            medicalExamController.updateMedicalExam(medicalExamId, endTime, startTime, description, title, price, tags, state);
            outcome = true;
        }
        return outcome;
    }

    /**
     * delete the Medical exam
     *
     * @param me the medical exam
     * @param id the id of the doctor
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteMedicalExam(MedicalExam me, int id) throws Exception {
        boolean outcome = false;
        if (me.getIdDoctor() == id && me.getStartTime().isBefore(LocalDateTime.now())) {
            if (me.getState() instanceof Booked) {
                if (me.getStartTime().isBefore(LocalDateTime.now())) {
                    String s = "The medical exam has been canceled";
                    State state = new Available();
                    //refund
                    double price = me.getPrice();
                    Customer customer = this.customerDao.get(me.getIdCustomer());
                    Doctor doctor = this.doctorDao.get(me.getIdDoctor());
                    doctor.setBalance(doctor.getBalance() - price);
                    customer.setBalance(customer.getBalance() + price);
                    this.customerDao.update(customer);
                    this.doctorDao.update(doctor);
                    medicalExamController.updateMedicalExam(me.getId(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice(),me.getTags(),state );
                }
            }
            medicalExamController.removeMedicalExam(me.getId());
            outcome = true;
        }
        return outcome;
    }

    /**
     * create a new Medical exam
     *
     * @param idDoctor    the id of the doctor
     * @param endTime     the end time of the medical exam
     * @param startTime   the start time of the medical exam
     * @param description the description of the medical exam
     * @param title       the title of the medical exam
     * @param price       the price of the medical exam
     */
    public void createMedicalExam(int idDoctor, LocalDateTime endTime, LocalDateTime startTime, String description, String title, double price) throws Exception {
        medicalExamController.addMedicalExam(idDoctor, endTime, startTime, description, title, price);
    }

    /**
     * get the Medical exams of a doctor
     *
     * @param id the id of the doctor
     * @return the list of medical exams
     */
    public List<MedicalExam> getMedicalExam(int id) throws Exception {
        return medicalExamController.getDoctorExams(id);
    }

    public List<Notification> getNotifications(int id) throws Exception {
        return notificationDao.getNotificationsByReceiverId(id);
    }

    public List<Document> getDocuments(int id) throws Exception {
        return documentDao.getByOwner(id);
    }

    private void addDocument(Document document) throws Exception {
        documentDao.insert(document);
    }

    public void addDocumentToMedicalExam(String title, String path, int ownerId, int receiverId, MedicalExam me) throws Exception {
        Document document = new Document(title, path, ownerId);
        if (me.getState() instanceof Booked) {
            document.setReceiverId(receiverId); // or me.getIdCustomer()
            Notification nd = new Notification("New document " + title + " by :" + ownerId, receiverId);
            notificationDao.insert(nd);
        }
        document.setMedicalExamId(me.getId());
        addDocument(document);
    }

    public void addDocumentToCustomer(String title, String path, int ownerId, int receiverId) throws Exception {
        Document document = new Document(title, path, ownerId);
        document.setReceiverId(receiverId);
        addDocument(document);
        Notification nd = new Notification("New document " + title + " by :" + ownerId, receiverId);
        notificationDao.insert(nd);
    }

    public void update(Doctor doctor) throws Exception {
        doctorDao.update(doctor);
    }

    /**
     * Modify the level of the customer
     *
     * @param customerId    customer id
     * @param level The new level
     * @return true if the level is modified, false otherwise
     */

    public boolean modifyCustomerLevel(int customerId, int level) throws Exception {
        Customer customer = this.customerDao.get(customerId);
        boolean outcome = false;
        if (customer.getLevel() != level) {
            customer.setLevel(level);
            customerDao.update(customer);
            outcome = true;
        }
        return outcome;
    }

}
