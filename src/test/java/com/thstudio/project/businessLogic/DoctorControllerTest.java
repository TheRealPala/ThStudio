package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.fixture.DoctorFixture;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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
        DoctorDao doctorDao = new MariaDbDoctorDao(new MariaDbPersonDao());
        doctorController = new DoctorController(doctorDao);
    }

    @Test
    void addDoctor() throws Exception {
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor());
        assertNotNull(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        Doctor addedDoctor = doctorController.getPerson(doctorToAdd.getId());
        assertEquals(doctorToAdd, addedDoctor);
    }

    @Test
    void updateDoctor() throws Exception {
        Doctor doctorToAdd = doctorController.addDoctor(DoctorFixture.genDoctor());
        doctorToAdd.setName("Luigi");
        doctorToAdd.setSurname("Bianchi");
        doctorToAdd.setDateOfBirth("2003-08-22");
        doctorToAdd.setBalance(350.55);
        doctorToAdd.setMedicalLicenseNumber("MLN-23485");
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
