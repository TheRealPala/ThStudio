package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.security.AuthorizedController;
import com.thstudio.project.security.LoginController;

import java.util.List;

public class CustomerController extends LoginController {

    private final CustomerDao customerDao;

    public CustomerController(CustomerDao customerDao, PersonDao personDao) throws Exception {
        super(personDao);
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
    public Customer addCustomer(String name, String surname, String dateOfBirth, int level, double balance, String email, String password, String token) throws Exception {
        super.validateToken(token);
        Customer c = new Customer(name, surname, dateOfBirth, level, balance, email, password);
        customerDao.insert(c);
        return c;
    }

    /**
     * Add a new customer
     *
     * @param customer The customer object
     * @return The customer added
     * @throws Exception bubbles up exceptions to PeopleController::addPerson()
     */
    public Customer addCustomer(Customer customer, String token) throws Exception {
        return this.addCustomer(customer.getName(), customer.getSurname(), customer.getDateOfBirth(), customer.getLevel(), customer.getBalance(), customer.getEmail(), customer.getPassword(), token);
    }

    /**
     * get the customer
     *
     * @param id The id of the customer
     * @return The customer
     */
    public Customer getCustomer(int id, String token) throws Exception {
        super.validateToken(token);
        return customerDao.get(id);
    }

    /**
     * get the customer
     *
     * @param customer The new customer
     */
    public void updateCustomer(Customer customer, String token) throws Exception {
        super.validateToken(token);
        customerDao.update(customer);
    }


    public boolean modifyCustomerLevel(int customerId, int level, String token) throws Exception {
        super.validateToken(token);
        Customer customer = customerDao.get(customerId);
        boolean outcome = false;
        if (customer.getLevel() != level) {
            customer.setLevel(level);
            customerDao.update(customer);
            outcome = true;
        }
        return outcome;
    }

    public boolean deleteCustomer(int id, String token) throws Exception {
        super.validateToken(token);
        return customerDao.delete(id);
    }

    public List<Customer> getAllCustomers(String token) throws Exception {
        super.validateToken(token);
        return customerDao.getAll();
    }
}