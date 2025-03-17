package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.CustomerDao;
import com.thstudio.project.dao.Database;
import com.thstudio.project.dao.MariaDbCustomerDao;
import com.thstudio.project.dao.MariaDbPersonDao;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.fixture.CustomerFixture;
import com.thstudio.project.fixture.PersonFixture;
import com.thstudio.project.security.LoginController;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private static LoginController loginController;
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
        loginController = new LoginController(personDao);
    }

    @Test
    void login() throws Exception {
        Person person = PersonFixture.genPerson();
        String plainPassword = person.getPassword();
        person.setPassword(loginController.hashPassword(plainPassword));
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

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
