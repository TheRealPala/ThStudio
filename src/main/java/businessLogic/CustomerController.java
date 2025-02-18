package businessLogic;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import dao.*;
import domainModel.*;
import domainModel.Search.Search;
import domainModel.State.Available;
import domainModel.State.Booked;

import static java.util.Collections.unmodifiableList;

public class CustomerController extends PersonController<Customer> {
    private final CustomerDao customerDao;
    private final NotificationDao notificationDao;
    private final DocumentDao documentDao;
    private final MedicalExamDao medicalExamDao;
    private final DoctorDao doctorDao;

    public CustomerController(CustomerDao customerDao, MedicalExamDao medicalExamDao, DoctorDao doctorDao,
                              NotificationDao notificationDao, DocumentDao documentDao) {
        super(customerDao);
        this.customerDao = customerDao;
        this.medicalExamDao = medicalExamDao;
        this.notificationDao = notificationDao;
        this.documentDao = documentDao;
        this.doctorDao = doctorDao;
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

    /**
     * search for the medical exams
     *
     * @param search The search object
     * @return The list of the medical exams
    */
    public List<MedicalExam> searchMedicalExam(Search search) throws Exception {
        return unmodifiableList(this.medicalExamDao.search(search));
    }

    /**
     * book a medical exam
     *
     * @param medicalExamId The medical exam id
     * @param customerId The customer id
     * @return true if the medical exam is canceled, false otherwise
     */
    public boolean bookMedicalExam(int medicalExamId, int customerId) throws Exception {
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        Customer c = this.customerDao.get(customerId);
        if (me.getState() instanceof Booked) {
            throw new RuntimeException("The exam you want to book is already booked");
        }
        if (c.getBalance() < me.getPrice()) {
            throw new RuntimeException("not enough money");
        } else {
            me.setState(new Booked());
            this.medicalExamDao.bookMedicalExam(me, customerId);
            //pay exam
            Doctor d = this.doctorDao.get(me.getIdDoctor());
            c.setBalance(c.getBalance() - me.getPrice());
            customerDao.update(c);
            d.setBalance(d.getBalance() + me.getPrice());
            doctorDao.update(d);
            Notification nd = new Notification("Booked exam " + me.getTitle() + "by :" + c.getName(), me.getIdDoctor());
            notificationDao.insert(nd);
        }
        return true;
    }

    /**
     * cancel a medical exam
     *
     * @param medicalExamId The medical exam id
     * @param customerId  The customer id
     * @return true if the medical exam is canceled, false otherwise
    */
    public boolean cancelMedicalExam(int medicalExamId, int customerId) throws Exception {
        MedicalExam me = this.medicalExamDao.get(medicalExamId);
        Customer c = this.customerDao.get(customerId);
        if (me.getIdCustomer() != c.getId()) {
            throw new RuntimeException("Unauthorized request");
        }
        if (me.getState() instanceof Booked && me.getIdCustomer() == c.getId()) {
            if (me.getStartTime().isBefore(LocalDateTime.now())) {
                //refund
                double medicalExamPrice = me.getPrice();
                Doctor d = this.doctorDao.get(me.getIdDoctor());
                d.setBalance(d.getBalance() - medicalExamPrice);
                c.setBalance(c.getBalance() + medicalExamPrice);
                this.doctorDao.update(d);
                this.customerDao.update(c);
            } else {
                System.out.println("no refund");
            }
            me.setState(new Available());
            this.medicalExamDao.update(me);
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
     * @param customerId The customer
     * @return The list of the medical exams
    */
    public List<MedicalExam> getCustomerExams(int customerId) throws Exception {
        return this.medicalExamDao.getCustomerExams(customerId);
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
}