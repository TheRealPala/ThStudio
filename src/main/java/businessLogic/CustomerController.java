package businessLogic;
import java.time.LocalDateTime;
import java.util.List;
import dao.CustomerDao;
import domainModel.Customer;
import domainModel.MedicalExam;
import domainModel.Search.Search;
import domainModel.State.Available;
import domainModel.State.Booked;

public class CustomerController extends PersonController<Customer> {
    private MedicalExamController mec;
    private DoctorController d;
    private CustomerDao customerDao;
    public CustomerController(CustomerDao customer, MedicalExamController mec, DoctorController doctor) {
        super(customer);
        this.mec = mec;
        this.d = doctor;
    }

    /**
     * Add a new customer
     * @param name                          The name of the customer
     * @param surname                       The surname of the customer
     * @param dateOfBirth                   The date of birth of the customer
     * @param iban                          The IBAN of the customer
     * @param level                         The level of the customer
     *
     * @return The CF of the newly created customer
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, int level) throws Exception {
        Customer c = new Customer(name, surname, dateOfBirth, iban, level);
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
            payment(c,me);
            //add a payment method
            d.getMedicalExam(me.getIdDoctor()).add(me); // consider adding a new in doctorDomainModel to add the medical exam
            // notify the doctor
            return true;
        }
    }
}