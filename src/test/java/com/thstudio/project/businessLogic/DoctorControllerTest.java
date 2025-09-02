package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.PersonFixture;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DoctorControllerTest {
    private static DoctorController doctorController;
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
        DoctorDao doctorDao = new MariaDbDoctorDao(personDao);
        loginController = new LoginController(personDao);
        doctorController = new DoctorController(doctorDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
        personDao.setAdmin(person, true);
    }

    @Test
    void addDoctor() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        assertNotNull(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        Doctor addedDoctor = doctorController.getDoctor(doctorToAdd.getId(), token);
        assertEquals(doctorToAdd, addedDoctor);
    }

    @Test
    void updateDoctor() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        doctorToAdd.setName("Luigi");
        doctorToAdd.setSurname("Bianchi");
        doctorToAdd.setDateOfBirth("2003-08-22");
        doctorToAdd.setBalance(350.55);
        doctorToAdd.setMedicalLicenseNumber("MLN-23485");
        doctorController.updateDoctor(doctorToAdd, token);
        Doctor updatedDoctor = doctorController.getDoctor(doctorToAdd.getId(), token);
        assertEquals(doctorToAdd, updatedDoctor);
    }

    @Test
    void deleteDoctor() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        doctorController.deleteDoctor(doctorToAdd.getId(), token);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> doctorController.getDoctor(doctorToAdd.getId(), token));
        assertEquals("The Doctor looked for in not present in the database", exception.getMessage());
        RuntimeException exception2 = assertThrows(RuntimeException.class,
                () -> personDao.get(doctorToAdd.getId()));
        assertEquals("The person looked for in not present in the database", exception2.getMessage());
    }
    @Test
    void getAllDoctors() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        assertEquals(2, doctorController.getAllPersons(token).size());
        doctorController.deleteDoctor(doctorToAdd.getId(), token);
        assertEquals(1, doctorController.getAllPersons(token).size());
    }

    @Test
    void useMethodsWithoutRequiredRole() throws Exception {
        Person doctorAdded = personDao.getPersonByUsername("test@test.com");
        personDao.setAdmin(doctorAdded, false);
        String token = loginController.login("test@test.com", "test");
        SecurityException excp = assertThrowsExactly(
                SecurityException.class,
                () -> doctorController.addDoctor(DoctorFixture.genDoctor(), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> doctorController.getAllPersons(token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        Doctor doctorFixture = DoctorFixture.genDoctor();
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> doctorController.addDoctor(doctorFixture, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        doctorFixture.setBalance(doctorFixture.getBalance() + 100);
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> doctorController.updateDoctor(doctorFixture, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> doctorController.deleteDoctor(doctorFixture.getId(), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
