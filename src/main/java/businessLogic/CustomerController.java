package businessLogic;
import dao.*;
import domainModel.*;

public class CustomerController extends PersonController<Customer> {
    private final CustomerDao customerDao;

    public CustomerController(CustomerDao customerDao) {
        super(customerDao);
        this.customerDao = customerDao;
    }

    /**
     * Add a new customer
     *
     * @param name        The name of the customer
     * @param surname     The surname of the customer
     * @param dateOfBirth The birthdate of the customer
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
     * get the customer
     *
     * @param id The id of the customer
     * @return The customer
    */
    public Customer getCustomer(int id) throws Exception {
        return this.customerDao.get(id);
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