package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.CustomerDao;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.security.Authn;
import com.thstudio.project.security.Authz;
import com.thstudio.project.security.JwtService;
import com.thstudio.project.security.LoginController;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class CustomerController {

    private final CustomerDao customerDao;
    private final Authz authz;
    private final Authn authn;
    
    public CustomerController(CustomerDao customerDao) throws Exception {
        this.customerDao = customerDao;
        this.authz = new Authz(new JwtService());
        this.authn = new Authn();
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
        authz.role(token); // valida e ricava il ruolo (non usato qui)
        Customer c = new Customer(name, surname, dateOfBirth, level, balance, email, this.authn.hashPassword(password));
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
        return this.addCustomer(customer.getName(), customer.getSurname(), customer.getDateOfBirth(), customer.getLevel(),
                customer.getBalance(), customer.getEmail(), this.authn.hashPassword(customer.getPassword()), token);
    }

    /**
     * get the customer
     *
     * @param id The id of the customer
     * @return The customer
     */
    public Customer getCustomer(int id, String token) throws Exception {
        authz.role(token);
        return customerDao.get(id);
    }

    /**
     * get the customer
     *
     * @param customer The new customer
     */
    public void updateCustomer(Customer customer, String token) throws Exception {
        authz.role(token);
        customerDao.update(customer);
    }

    public boolean modifyCustomerLevel(int customerId, int level, String token) throws Exception {
        authz.role(token);
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
        authz.role(token);
        return customerDao.delete(id);
    }

    public List<Customer> getAllCustomers(String token) throws Exception {
        authz.role(token);
        return customerDao.getAll();
    }
    
}