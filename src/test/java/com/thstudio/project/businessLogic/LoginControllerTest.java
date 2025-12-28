package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.Database;
import com.thstudio.project.dao.MariaDbCustomerDao;
import com.thstudio.project.dao.MariaDbDoctorDao;
import com.thstudio.project.dao.MariaDbPersonDao;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.fixture.CustomerFixture;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.PersonFixture;
import com.thstudio.project.security.Authn;
import com.thstudio.project.security.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private static LoginController loginController;
    private static MariaDbPersonDao personDao;
    private static MariaDbCustomerDao customerDao;
    private static MariaDbDoctorDao doctorDao;
    private static JwtService jwtService;
    private static Authn authn;

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
        customerDao = new MariaDbCustomerDao(personDao);
        doctorDao = new MariaDbDoctorDao(personDao);
        jwtService = new JwtService();
        authn = new Authn(jwtService);
        loginController = new LoginController(personDao, authn);
    }

    @Test
    void login() throws Exception {
        Person person = PersonFixture.genPerson();
        String plainPassword = person.getPassword();
        person.setPassword(authn.hashPassword(plainPassword));
        personDao.insert(person);

        String token = loginController.login(person.getEmail(), plainPassword);
        assertNotNull(token);
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);

        SecurityException thrown = assertThrowsExactly(
                SecurityException.class,
                () -> {
                    loginController.login(person.getEmail(), "wrongPassword");
                }
        );
        assertEquals("Invalid password", thrown.getMessage());
    }

    @Test
    void loginWithNonExistentUser() {
        SecurityException thrown = assertThrowsExactly(
                SecurityException.class,
                () -> {
                    loginController.login("nonExistentUser", "wrongPassword");
                }
        );
        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void loginWithAdminUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        String plainPassword = person.getPassword();
        person.setPassword(authn.hashPassword(plainPassword));
        personDao.insert(person);
        personDao.setAdmin(person, true);
        String token = loginController.login(person.getEmail(), plainPassword);
        assertNotNull(token);
        String role = jwtService.getRole(token);
        assertEquals("admin", role);
    }

    @Test
    void loginWithDoctorUser() throws Exception {
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        String plainPassword = doctorToAdd.getPassword();
        doctorToAdd.setPassword(authn.hashPassword(plainPassword));
        doctorDao.insert(doctorToAdd);
        String token = loginController.login(doctorToAdd.getEmail(), plainPassword);
        assertNotNull(token);
        String role = jwtService.getRole(token);
        assertEquals("doctor", role);
    }

    @Test
    void loginWithCustomerUser() throws Exception {
        Customer customerToAdd = CustomerFixture.genCustomer();
        String plainPassword = customerToAdd.getPassword();
        customerToAdd.setPassword(authn.hashPassword(plainPassword));
        customerDao.insert(customerToAdd);
        String token = loginController.login(customerToAdd.getEmail(), plainPassword);
        assertNotNull(token);
        String role = jwtService.getRole(token);
        assertEquals("customer", role);
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
