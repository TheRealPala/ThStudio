package businessLogic;
import java.time.LocalDateTime;
import java.util.List;
import dao.CustomerDao;
import dao.DoctorDao;
import dao.MariaDbNotificationDao;
import dao.MedicalExamDao;
import domainModel.Customer;
import domainModel.Doctor;
import domainModel.MedicalExam;
import domainModel.Notification;
import domainModel.Search.Search;
import domainModel.State.Available;
import domainModel.State.Booked;

public class CustomerController extends PersonController<Customer> {
    private MedicalExamController mec;
    private DoctorController doctorController;
    private CustomerDao customerDao;
    private MariaDbNotificationDao notification;
    public CustomerController(CustomerDao customer, MedicalExamDao me, DoctorDao d, CustomerDao customerDao,MariaDbNotificationDao nd) {
        super(customer);
        this.mec = new MedicalExamController(me ,d , this, nd);
        this.doctorController = new DoctorController(d,mec,nd);
        this.customerDao = customerDao;
        notification =nd;



    }

    /**
     * Add a new customer
     * @param name                          The name of the customer
     * @param surname                       The surname of the customer
     * @param dateOfBirth                   The date of birth of the customer
     * @param level                         The level of the customer
     * @param balance                       The balance of the customer
     *
     * @return The CF of the newly created customer
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, int level, double balance) throws Exception {
        Customer c = new Customer(name, surname, dateOfBirth, level, balance);
        return super.addPerson(c);
    }
    /**
     * Modify the level of the customer
     * @param c The customer
     * @param level The new level
     * @return true if the level is modified, false otherwise
     * @throws Exception
     */

    public boolean modifyLevel(Customer c, int level) throws Exception {
        if(c.getLevel() != level){
            c.setLevel(level);
            customerDao.update(c);
            return true;
        }
        else{
            return false;
        }
    }
    /** search for the medical exams
     * @param search The search object
     * @return The list of the medical exams
     * @throws Exception
     */
    public List<MedicalExam> searchMedicalExam(Search search) throws Exception {
        return mec.search(search);
    }
    /** book a medical exam
     * @param me The medical exam
     * @param c The customer
     * @return true if the medical exam is booked, false otherwise
     * @throws Exception
     */
    public boolean bookMedicalExam(MedicalExam me,Customer c ) throws Exception {
        if(me.getState() instanceof Booked ){
            return false;
        }
        else{
            me.setState(new Booked());
            mec.updateMedicalExam(me.getId(),c.getId(),me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());
            payment(c,me,me.getIdDoctor());

            doctorController.getMedicalExam(me.getIdDoctor()).add(me);
            Notification nd =new Notification("Booked exam "+ me.getTitle()+"by :"+c.getName(),me.getIdDoctor());
            notification.insert(nd);
            return true;
        }

    }
    /** cancel a medical exam
     * @param me The medical exam
     * @param c The customer
     * @return true if the medical exam is canceled, false otherwise
     * @throws Exception
     */
    public boolean cancelMedicalExam(MedicalExam me, Customer c) throws Exception {
        if(me.getState() instanceof Booked && me.getIdCustomer()== c.getId()){
            if(me.getStartTime().isBefore(LocalDateTime.now())){
                mec.refund(c.getId(),me);    // may as well use de payment function
            }
            else {
                System.out.println("no refund");
            }
            me.setState(new Available());
            mec.updateMedicalExam(me.getId(),c.getId(),me.getIdDoctor(), me.getEndTime(), me.getStartTime(), me.getDescription(), me.getTitle(), me.getPrice());

            Notification nd =new Notification("Deleted exam "+ me.getTitle()+"by :"+c.getName(),me.getIdDoctor());
            notification.insert(nd);
            return true;
        }
        else{
            return false;
        }
    }
    /** get the list of the medical exams
     * @param c The customer
     * @return The list of the medical exams
     * @throws Exception
     */
    public List<MedicalExam> examList (int c) throws Exception {
        return this.mec.getCustomerExams(c);
    }
    /** payment of the medical exam
     * @param c The customer
     * @param me The medical exam
     * @throws Exception
     */
    public void payment(Customer c, MedicalExam me, int id) throws Exception {
        Doctor d = doctorController.getPerson(id);
        if(c.getBalance()< me.getPrice()){
            throw new RuntimeException(" not enough balance");
        }
        else{
            if(me.getState()instanceof Booked && me.getIdCustomer() == c.getId()){
                c.setBalance(c.getBalance()-me.getPrice());
                d.setBalance(d.getBalance()+me.getPrice());
            }
            else {
                throw new RuntimeException(" not your medical exam");
            }
        }
    }
    /** get the customer
     * @param id The id of the customer
     * @return The customer
     * @throws Exception
     */
    public Customer getCustomer(int id) throws Exception {
        return this.customerDao.get(id);
    }

    public List<Notification> getNotifications(int id) throws Exception {
        return notification.getNotificationsByReceiverId(id);
    }



    // add search of the medical exams
    //add book medical exams, and a payment method, notify the doctor
    // add cancel medical exams, and notify the doctor, and define a policy for the refund
    // add a way to get the list of the medical exams
}