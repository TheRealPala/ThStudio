package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.DoctorDao;
import com.thstudio.project.dao.Database;
import com.thstudio.project.dao.MariaDbDoctorDao;
import com.thstudio.project.dao.MariaDbPersonDao;
import static org.junit.jupiter.api.Assertions.*;

import com.thstudio.project.domainModel.Doctor;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

class DoctorControllerTest {
    private static DoctorController doctorController;

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
        DoctorDao customerDao = new MariaDbDoctorDao(new MariaDbPersonDao());
        doctorController = new DoctorController(customerDao);
    }

    @Test
    void addDoctor() throws Exception {
        Doctor doctorToAdd = doctorController.addDoctor("Marco", "Rossi", "2000-10-01", "ddd", 1000);
        assertNotNull(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        Doctor addedDoctor = doctorController.getPerson(doctorToAdd.getId());
        assertEquals(doctorToAdd, addedDoctor);
    }

    @Test
    void updateDoctor() throws Exception {
        Doctor doctorToAdd = doctorController.addDoctor("Marco", "Rossi", "2000-10-01", "ddd", 1000);
        doctorToAdd.setName("Luigi");
        doctorToAdd.setSurname("Bianchi");
        doctorToAdd.setDateOfBirth("2003-08-22");
        doctorToAdd.setMedicalLicenseNumber("eee");
        doctorToAdd.setBalance(350.55);
        doctorController.updateDoctor(doctorToAdd);
        Doctor updatedDoctor = doctorController.getDoctor(doctorToAdd.getId());
        assertEquals(doctorToAdd, updatedDoctor);
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
