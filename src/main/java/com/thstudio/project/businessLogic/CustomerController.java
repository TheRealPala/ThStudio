package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;

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
     * @return The customer added
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Customer addCustomer(String name, String surname, String dateOfBirth, int level, double balance) throws Exception {
        Customer c = new Customer(name, surname, dateOfBirth, level, balance);
        super.addPerson(c);
        return c;
    }

    /**
     * Add a new customer
     *
     * @param customer The customer object
     * @return The customer added
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Customer addCustomer(Customer customer) throws Exception {
        return this.addCustomer(customer.getName(), customer.getSurname(), customer.getDateOfBirth(), customer.getLevel(), customer.getBalance());
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
     * get the customer
     *
     * @param customer The new customer
     */
    public void updateCustomer(Customer customer) throws Exception {
        this.customerDao.update(customer);
    }


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