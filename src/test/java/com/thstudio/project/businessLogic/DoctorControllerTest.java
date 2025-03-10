package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.fixture.CustomerFixture;
import com.thstudio.project.fixture.DoctorFixture;
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
        doctorController = new DoctorController(doctorDao, personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Customer customer = CustomerFixture.genTestCustomer();
        personDao.insert(customer);
    }

    @Test
    void addDoctor() throws Exception {
        String token = doctorController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        assertNotNull(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        Doctor addedDoctor = doctorController.getDoctor(doctorToAdd.getId(), token);
        assertEquals(doctorToAdd, addedDoctor);
    }

    @Test
    void updateDoctor() throws Exception {
        String token = doctorController.login("test@test.com", "test");
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
        String token = doctorController.login("test@test.com", "test");
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
        String token = doctorController.login("test@test.com", "test");
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        doctorController.addDoctor(DoctorFixture.genDoctor(), token);
        assertEquals(2, doctorController.getAllPersons(token).size());
        doctorController.deleteDoctor(doctorToAdd.getId(), token);
        assertEquals(1, doctorController.getAllPersons(token).size());
    }


    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
