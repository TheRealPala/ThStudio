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

    @BeforeAll
    static void setDatabaseSettings() {
        Dotenv dotenv = Dotenv.configure().directory("config").load();
        Database.setDbHost(dotenv.get("DB_HOST"));
        Database.setDbName(dotenv.get("DB_NAME_DEFAULT"));
        Database.setDbTestName(dotenv.get("DB_NAME_TEST"));
        Database.setDbUser(dotenv.get("DB_USER"));
        Database.setDbPassword(dotenv.get("DB_PASSWORD"));
        Database.setDbPort(dotenv.get("DB_PORT"));
        assertTrue((Database.testConnection(true, false)));
        CustomerDao customerDao = new MariaDbCustomerDao(new MariaDbPersonDao());
        customerController = new CustomerController(customerDao);
    }

    @Test
    void addCustomer() throws Exception {
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer());
        assertNotNull(customerToAdd);
        assertNotEquals(customerToAdd.getId(), 0);
        Customer addedCustomer = customerController.getPerson(customerToAdd.getId());
        assertEquals(customerToAdd, addedCustomer);
    }

    @Test
    void modifyCustomerLevel() throws Exception {
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer());
        assertEquals(customerToAdd.getLevel(), customerController.getPerson(customerToAdd.getId()).getLevel());
        customerToAdd.setLevel(customerToAdd.getLevel() + 1);
        assertTrue(customerController.modifyCustomerLevel(customerToAdd.getId(), customerToAdd.getLevel()));
        Customer updatedCustomer = customerController.getCustomer(customerToAdd.getId());
        assertNotNull(updatedCustomer);
        assertEquals(updatedCustomer.getLevel(), customerToAdd.getLevel());
    }

    @Test
    void updateCustomer() throws Exception {
        Customer customerToAdd = customerController.addCustomer(CustomerFixture.genCustomer());
        customerToAdd.setName("Luigi");
        customerToAdd.setSurname("Bianchi");
        customerToAdd.setDateOfBirth("2003-08-22");
        customerToAdd.setLevel(2);
        customerToAdd.setBalance(350.55);
        customerController.updateCustomer(customerToAdd);
        Customer updatedCustomer = customerController.getCustomer(customerToAdd.getId());
        assertEquals(customerToAdd, updatedCustomer);
    }


    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
