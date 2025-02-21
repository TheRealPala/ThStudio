package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MedicalExamControllerTest {
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
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
        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        NotificationDao notificationDao = new MariaDbNotificationDao();
        PersonDao personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
    }

    @Test
    void addMedicalExam() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExamToAdd = medicalExamController.addMedicalExam(doctorToAdd.getId(), "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(medicalExamToAdd.getId(), 0);
        MedicalExam medicalExamAdded = medicalExamController.getExam(medicalExamToAdd.getId());
        assertEquals(medicalExamAdded, medicalExamToAdd);
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
    }
}
