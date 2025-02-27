package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;

import java.util.List;

public class CustomerController extends PersonController<Customer> {

    public CustomerController(CustomerDao customerDao) {
        super(customerDao);
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
        return super.getPerson(id);
    }

    /**
     * get the customer
     *
     * @param customer The new customer
     */
    public void updateCustomer(Customer customer) throws Exception {
        super.updatePerson(customer);
    }


    public boolean modifyCustomerLevel(int customerId, int level) throws Exception {
        Customer customer = super.getPerson(customerId);
        boolean outcome = false;
        if (customer.getLevel() != level) {
            customer.setLevel(level);
            super.updatePerson(customer);
            outcome = true;
        }
        return outcome;
    }
    public boolean deleteCustomer(int id) throws Exception {
        return super.deletePerson(id);
    }
    public List<Customer> getAllCustomers() throws Exception {
        return super.getAllPersons();
    }
}