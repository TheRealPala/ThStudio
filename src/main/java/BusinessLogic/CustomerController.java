package BusinessLogic;

import dao.CustomerDao;
import domainModel.Customer;

public class CustomerController extends PersonController<Customer> {
    public CustomerController(CustomerDao customerDAO){
        super(customerDAO);
    }

    /**
     * Add a new customer
     *
     * @return The CF of the newly created customer
     * @throws Exception bubbles up exceptions of PeopleController::addPerson()
     */
    public String addPerson(String name, String surname, String dateOfBirth, String iban, int level) throws Exception {
        Customer c = new Customer(name, surname, dateOfBirth, iban, level);
        return super.addPerson(c);
    }
}