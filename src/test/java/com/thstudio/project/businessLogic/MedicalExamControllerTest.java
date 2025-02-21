package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MedicalExamControllerTest {
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
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
        customerDao = new MariaDbCustomerDao(personDao);
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
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), 0, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(secondDoctor.getId(), 0, "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000));
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
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), 0,"Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(firstDoctor.getId(), 0,"Titolo Seconda Visita", "Dieta", "2000-10-01 16:30:00", "2000-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getDoctorExams(firstDoctor.getId()).size());
    }

    @Test
    void getCustomerMedicalExam() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getCustomerExams(customer.getId()).size());
    }

    @Test
    void addMedicalExam() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExamToAdd = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(medicalExamToAdd.getId(), 0);
        MedicalExam medicalExamAdded = medicalExamController.getExam(medicalExamToAdd.getId());
        assertEquals(medicalExamAdded, medicalExamToAdd);
    }

    @Test
    void addMedicalExamOfANonExistingDoctor() {
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.addMedicalExam(1, 0, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");

    }

    @Test
    void addMedicalExamWhenTheDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0,  "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(firstMedicalExam, 0);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.addMedicalExam(doctorToAdd.getId(), 0,  "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The given doctor is already occupied in the given time range");
    }

    @Test
    void updateMedicalExam() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0, "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000);
        assertNotEquals(medicalExam.getId(), 0);

        medicalExam.setTitle("Titolo Modificato");
        medicalExam.setDescription("Anamnesi dopo primo check");
        medicalExam.setStartTimeFromString("2025-10-01 16:30:00");
        medicalExam.setEndTimeFromString("2025-10-01 17:30:00");
        medicalExam.setPrice(2000);
        boolean outcomeUpdate = medicalExamController.updateMedicalExam(medicalExam);
        assertTrue(outcomeUpdate);
    }

    @Test
    void updateMedicalExamWhenDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0, "Visita 1", "Rilevazioni antropometriche", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 300);
        assertNotEquals(firstMedicalExam.getId(), 0);
        MedicalExam secondMedicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0,  "Visita 2", "Seduta chinesiologica", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 500);
        secondMedicalExam.setStartTimeFromString("2025-10-01 16:00:00");
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.updateMedicalExam(secondMedicalExam);
                }
        );
        assertEquals(thrown.getMessage(), "The given doctor is already occupied in the given time range");
    }

    @Test
    void updateMedicalExamAfterStartTime() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        LocalDateTime now = LocalDateTime.now();
        MedicalExam medicalExam = medicalExamController.addMedicalExam(doctorToAdd.getId(), 0,  "Visita 1", "Rilevazioni antropometriche",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                now.plusHours(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 300);

        medicalExam.setTitle("Titolo Modificato");
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.updateMedicalExam(medicalExam);
                }
        );
        assertEquals(thrown.getMessage(), "Forbidden! Can't update an exam already started");
    }

    @Test
    void getMedicalExamByState() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(medicalExamController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Available()).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam: addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            medicalExamController.updateMedicalExam(addedMedicalExam);
        }

        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Booked(now)).size());

    }



    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
    }
}
