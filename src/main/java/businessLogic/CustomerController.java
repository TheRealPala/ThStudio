package businessLogic;

import dao.CustomerDao;
import domainModel.Customer;

public class CustomerController extends PersonController<Customer> {
    public CustomerController(CustomerDao customerDAO){
        super(customerDAO);
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
    //TODO: add search of the medical exams
    //TODO: add book medical exams, and a payment method, notify the doctor
    //TODO: add cancel medical exams, and notify the doctor, and define a policy for the refund
    //TODO: add a way to get the list of the medical exams
}