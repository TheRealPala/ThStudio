package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.CustomerDao;
import com.thstudio.project.dao.Database;
import com.thstudio.project.dao.MariaDbCustomerDao;
import com.thstudio.project.dao.MariaDbPersonDao;

import static org.junit.jupiter.api.Assertions.*;

import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.fixture.CustomerFixture;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

class CustomerControllerTest {
    private static CustomerController customerController;
    private static MariaDbPersonDao personDao;

    @BeforeAll
    static void setDatabaseSettings() throws Exception {
        Dotenv dotenv = Dotenv.configure().directory("config").load();
        Database.setDbHost(dotenv.get("DB_HOST"));
        Database.setDbName(dotenv.get("DB_NAME_DEFAULT"));
        Database.setDbTestName(dotenv.get("DB_NAME_TEST"));
        Database.setDbUser(dotenv.get("DB_USER"));
        Database.setDbPassword(dotenv.get("DB_PASSWORD"));
        Database.setDbPort(dotenv.get("DB_PORT"));
        assertTrue((Database.testConnection(true, false)));
        personDao = new MariaDbPersonDao();
        CustomerDao customerDao = new MariaDbCustomerDao( personDao);
        customerController = new CustomerController(customerDao, personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Customer customer = CustomerFixture.genTestCustomer();
        personDao.insert(customer);
    }

    @Test
    void addCustomer() throws Exception {
        String token = customerController.login("test@test.com", "test");
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer(), token);
        assertNotNull(customerToAdd);
        assertNotEquals(customerToAdd.getId(), 0);
        Customer addedCustomer = customerController.getCustomer(customerToAdd.getId(), token);
        assertEquals(customerToAdd, addedCustomer);
    }

    @Test
    void modifyCustomerLevel() throws Exception {
        String token = customerController.login("test@test.com", "test");
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer(), token);
        assertEquals(customerToAdd.getLevel(), customerController.getCustomer(customerToAdd.getId(), token).getLevel());
        customerToAdd.setLevel(customerToAdd.getLevel() + 1);
        assertTrue(customerController.modifyCustomerLevel(customerToAdd.getId(), customerToAdd.getLevel(), token));
        Customer updatedCustomer = customerController.getCustomer(customerToAdd.getId(), token);
        assertNotNull(updatedCustomer);
        assertEquals(updatedCustomer.getLevel(), customerToAdd.getLevel());
    }

    @Test
    void updateCustomer() throws Exception {
        String token = customerController.login("test@test.com", "test");
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer(), token);
        customerToAdd.setName("Luigi");
        customerToAdd.setSurname("Bianchi");
        customerToAdd.setDateOfBirth("2003-08-22");
        customerToAdd.setLevel(2);
        customerToAdd.setBalance(350.55);
        customerController.updateCustomer(customerToAdd, token);
        Customer updatedCustomer = customerController.getCustomer(customerToAdd.getId(), token);
        assertEquals(customerToAdd, updatedCustomer);
    }
    @Test
    void deleteCustomer() throws Exception {
        String token = customerController.login("test@test.com", "test");
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer(), token);
        customerController.deleteCustomer(customerToAdd.getId(), token);
        RuntimeException exception = assertThrowsExactly(RuntimeException.class,
                () -> customerController.getCustomer(customerToAdd.getId(), token));
        assertEquals("The Customer looked for in not present in the database", exception.getMessage());
        RuntimeException exception2 = assertThrowsExactly(RuntimeException.class,
                () -> personDao.get(customerToAdd.getId()));
        assertEquals("The person looked for in not present in the database", exception2.getMessage());
    }

    @Test
    void getAllCustomers() throws Exception {
        String token = customerController.login("test@test.com", "test");
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer(), token);
        customerController.addCustomer(CustomerFixture.genCustomer(), token);
        assertEquals(2, customerController.getAllCustomers(token).size());
        customerController.deleteCustomer(customerToAdd.getId(), token);
        assertEquals(1, customerController.getAllCustomers(token).size());
    }


    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
