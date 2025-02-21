package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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
    void getAllMedicalExams() throws Exception {

        Doctor firstDoctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);
        Doctor secondDoctor = new Doctor("Giovanni", "Verdi", "1990-11-23", "MLN-25220", 25000);
        doctorDao.insert(secondDoctor);
        assertNotEquals(secondDoctor.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(secondDoctor.getId(), "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getAll().size());

    }
    @Test
    void getDoctorMedicalExam() throws Exception {
        Doctor firstDoctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getDoctorExams(firstDoctor.getId()).size());
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

    @Test
    void addMedicalExamOfANonExistingDoctor() {
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.addMedicalExam(1, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");

    }

    @Test
    void addMedicalExamWhenTheDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(firstMedicalExam, 0);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.addMedicalExam(doctorToAdd.getId(), "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The given doctor is already occupied in the given time range");
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
    }
}
