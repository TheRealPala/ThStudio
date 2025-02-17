package businessLogic;

import java.time.LocalDateTime;
import java.util.List;

import dao.*;
import domainModel.*;
import domainModel.Search.Search;
import domainModel.State.Available;
import domainModel.State.Booked;

public class CustomerController extends PersonController<Customer> {
    private final MedicalExamController medicalExamController;
    private final DoctorController doctorController;
    private final CustomerDao customerDao;
    private final NotificationDao notificationDao;
    private final DocumentDao documentDao;
    private final MedicalExamDao medicalExamDao;

    public CustomerController(MedicalExamController medicalExamController, DoctorController doctorController,
                              CustomerDao customerDao, MedicalExamDao medicalExamDao,
                              NotificationDao notificationDao, DocumentDao documentDao) {
        super(customerDao);
        this.medicalExamController = medicalExamController;
        this.doctorController = doctorController;
        this.customerDao = customerDao;
        this.medicalExamDao = medicalExamDao;
        this.notificationDao = notificationDao;
        this.documentDao = documentDao;
    }

    /**
     * Add a new customer
     *
     * @param name        The name of the customer
     * @param surname     The surname of the customer
     * @param dateOfBirth The date of birth of the customer
     * @param level       The level of the customer
     * @param balance     The balance of the customer
     * @return The CF of the newly created customer
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
    */
    public String addCustomer(String name, String surname, String dateOfBirth, int level, double balance) throws Exception {
        Customer c = new Customer(name, surname, dateOfBirth, level, balance);
        return super.addPerson(c);
    }

    //TODO MOVE INTO DOCTOR CONTROLLER
    /**
     * Modify the level of the customer
     *
     * @param customerId    customer id
     * @param level The new level
     * @return true if the level is modified, false otherwise
     */

    public boolean modifyCustomerLevel(int customerId, int level) throws Exception {

        Customer c = this.customerDao.get(customerId);
        if (c.getLevel() != level) {
            c.setLevel(level);
            customerDao.update(c);
            return true;
        } else {
            return false;
        }
    }

    /**
     * search for the medical exams
     *
     * @param search The search object
     * @return The list of the medical exams
    */
    public List<MedicalExam> searchMedicalExam(Search search) throws Exception {
        return medicalExamController.search(search);
    }

    /**
     * book a medical exam
     *
     * @param medicalExamId The medical exam id
     * @param customerId The customer id
     * @return true if the medical exam is booked, false otherwise
    */
    public boolean bookMedicalExam(int medicalExamId, int customerId) throws Exception {
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        Customer c = this.customerDao.get(customerId);
        if (me.getState() instanceof Booked) {
            return false;
        } else {
            me.setState(new Booked());
            //TODO L'id del cliente non deve essere nel costruttore dell'esame medico (un esame medico che ancora non è stato prenotato, non ha nessun cliente)
            //riparti da qui
            medicalExamController.updateMedicalExam(me.getId(), c.getId(), me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());
            payExam(c, me);
            Notification nd = new Notification("Booked exam " + me.getTitle() + "by :" + c.getName(), me.getIdDoctor());
            notificationDao.insert(nd);
            return true;
        }

    }

    /**
     * cancel a medical exam
     *
     * @param me The medical exam
     * @param c  The customer
     * @return true if the medical exam is canceled, false otherwise
    */
    public boolean cancelMedicalExam(MedicalExam me, Customer c) throws Exception {
        if (me.getState() instanceof Booked && me.getIdCustomer() == c.getId()) {
            if (me.getStartTime().isBefore(LocalDateTime.now())) {
                medicalExamController.refund(c.getId(), me);    // may as well use the payment function
            } else {
                System.out.println("no refund");
            }
            me.setState(new Available());
            medicalExamController.updateMedicalExam(me.getId(), c.getId(), me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());

            Notification nd = new Notification("Deleted exam " + me.getTitle() + "by :" + c.getName(), me.getIdDoctor());
            notificationDao.insert(nd);
            return true;
        } else {
            return false;
        }
    }

    /**
     * get the list of the medical exams
     *
     * @param c The customer
     * @return The list of the medical exams
    */
    public List<MedicalExam> examList(int c) throws Exception {
        return this.medicalExamController.getCustomerExams(c);
    }

    /**
     * payment of the medical exam
     *
     * @param c  The customer
     * @param me The medical exam
     */
    public void payExam(Customer c, MedicalExam me) throws Exception {
        Doctor d = doctorController.getPerson(me.getIdDoctor());
        if (c.getBalance() < me.getPrice()) {
            throw new RuntimeException("not enough money");
        } else {
            if (me.getState() instanceof Booked && me.getIdCustomer() == c.getId()) {
                c.setBalance(c.getBalance() - me.getPrice());
                customerDao.update(c);
                d.setBalance(d.getBalance() + me.getPrice());
                doctorController.update(d);
            } else {
                throw new RuntimeException(" not your medical exam");
            }
        }
    }

    /**
     * get the customer
     *
     * @param id The id of the customer
     * @return The customer
    */
    public Customer getCustomer(int id) throws Exception {
        return this.customerDao.get(id);
    }

    public List<Notification> getNotifications(int id) throws Exception {
        return notificationDao.getNotificationsByReceiverId(id);
    }

    public List<Document> getDocuments(int id) throws Exception {
        return documentDao.getByReceiver(id);
    }


    // add search of the medical exams
    //add book medical exams, and a payment method, notify the doctor
    // add cancel medical exams, and notify the doctor, and define a policy for the refund
    // add a way to get the list of the medical exams
}