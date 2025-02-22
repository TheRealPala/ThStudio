package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Search.*;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StateControllerTest {
    private static StateController stateController;
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
        PersonDao personDao = new MariaDbPersonDao();
        customerDao = new MariaDbCustomerDao(personDao);
        doctorDao = new MariaDbDoctorDao(personDao);
        NotificationDao notificationDao = new MariaDbNotificationDao();
        stateController = new StateController(medicalExamDao, customerDao, doctorDao, notificationDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
    }
    @Test
    void bookMedicalExam() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000);
        assertNotEquals(addedMedicalExam.getId(), 0);
        stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId());
        MedicalExam bookedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
        assertInstanceOf(Booked.class, bookedMedicalExam.getState());
        assertEquals(bookedMedicalExam.getIdCustomer(), customer.getId());
    }

   /* @Test
    void addMedicalExam() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExamToAdd = stateController.addMedicalExam(doctorToAdd.getId(), 0, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(medicalExamToAdd.getId(), 0);
        MedicalExam medicalExamAdded = stateController.getExam(medicalExamToAdd.getId());
        assertEquals(medicalExamAdded, medicalExamToAdd);
    }

    @Test
    void addMedicalExamOfANonExistingDoctor() {
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.addMedicalExam(1, 0, "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");

    }

    @Test
    void addMedicalExamWhenTheDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = stateController.addMedicalExam(doctorToAdd.getId(), 0,  "Titolo Visita", "Chek-up", "2000-10-01 15:30:00", "2000-10-01 16:30:00", 1000);
        assertNotEquals(firstMedicalExam, 0);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    stateController.addMedicalExam(doctorToAdd.getId(), 0,  "Titolo Seconda Visita", "Dieta", "2000-10-01 15:45:00", "2000-10-01 17:30:00", 1000);
                }
        );
        assertEquals(thrown.getMessage(), "The given doctor is already occupied in the given time range");
    }

    @Test
    void updateMedicalExam() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExam = stateController.addMedicalExam(doctorToAdd.getId(), 0, "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000);
        assertNotEquals(medicalExam.getId(), 0);

        medicalExam.setTitle("Titolo Modificato");
        medicalExam.setDescription("Anamnesi dopo primo check");
        medicalExam.setStartTimeFromString("2025-10-01 16:30:00");
        medicalExam.setEndTimeFromString("2025-10-01 17:30:00");
        medicalExam.setPrice(2000);
        boolean outcomeUpdate = stateController.updateMedicalExam(medicalExam);
        assertTrue(outcomeUpdate);
    }

    @Test
    void updateMedicalExamWhenDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = stateController.addMedicalExam(doctorToAdd.getId(), 0, "Visita 1", "Rilevazioni antropometriche", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 300);
        assertNotEquals(firstMedicalExam.getId(), 0);
        MedicalExam secondMedicalExam = stateController.addMedicalExam(doctorToAdd.getId(), 0,  "Visita 2", "Seduta chinesiologica", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 500);
        secondMedicalExam.setStartTimeFromString("2025-10-01 16:00:00");
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    stateController.updateMedicalExam(secondMedicalExam);
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
        MedicalExam medicalExam = stateController.addMedicalExam(doctorToAdd.getId(), 0,  "Visita 1", "Rilevazioni antropometriche",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                now.plusHours(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 300);

        medicalExam.setTitle("Titolo Modificato");
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.updateMedicalExam(medicalExam);
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
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam.getId(), 0);
        }
        assertEquals(addedMedicalExams.size(), stateController.getExamsByState(new Available()).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam: addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            stateController.updateMedicalExam(addedMedicalExam);
        }

        assertEquals(addedMedicalExams.size(), stateController.getExamsByState(new Booked(now)).size());
    }

    @Test
    void searchByOnlineTag() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag isOnlineTag = new TagIsOnline("yes");
        Tag inPersonTag = new TagIsOnline("no");
        tagDao.insert(isOnlineTag);
        tagDao.insert(inPersonTag);
        tagDao.attachTagToMedicalExam(firstMedicalExam, isOnlineTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, inPersonTag);

        Search onlineExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "yes");
        List<MedicalExam> onlineExams = stateController.search(onlineExamSearch);
        assertEquals(onlineExams.size(), 1);

        Search inPersonExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "no");
        List<MedicalExam> inPersonExams = stateController.search(inPersonExamSearch);
        assertEquals(inPersonExams.size(), 1);
    }

    @Test
    void searchByPrice() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000);
        stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 5000);

        Search priceBelowOrEqualToOneThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 1000);
        List<MedicalExam> examsBelowOneThousand = stateController.search(priceBelowOrEqualToOneThousandSearch);
        assertEquals(examsBelowOneThousand.size(), 1);

        Search priceBelowOrEqualToFiveThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 5000);
        List<MedicalExam> examsBelowFiveThousand = stateController.search(priceBelowOrEqualToFiveThousandSearch);
        assertEquals(examsBelowFiveThousand.size(), 2);
    }

    @Test
    void searchByStartTime() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-01-01 15:30:00", "2025-01-01 16:30:00", 1000);
        stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-02-01 16:30:00", "2025-02-01 17:30:00", 5000);

        Search startAfterOrInJanuarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-01-01 00:00:00");
        List<MedicalExam> examsAfterOrInJanuary = stateController.search(startAfterOrInJanuarySearch);
        assertEquals(examsAfterOrInJanuary.size(), 2);
        
        Search startAfterOrInFebruarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-02-01 00:00:00");
        List<MedicalExam> examsAfterOrInFebruary = stateController.search(startAfterOrInFebruarySearch);
        assertEquals(examsAfterOrInFebruary.size(), 1);
        
    }

    @Test
    void searchByState() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        Search examsAvailableSearch = new DecoratorSearchState(new SearchConcrete(), new Available().getState());
        assertEquals(addedMedicalExams.size(), stateController.search(examsAvailableSearch).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam: addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            stateController.updateMedicalExam(addedMedicalExam);
        }

        Search examsBookedSearch = new DecoratorSearchState(new SearchConcrete(), new Booked(now).getState());
        assertEquals(addedMedicalExams.size(), stateController.search(examsBookedSearch).size());
    }

    @Test
    void searchByTypeTag() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag checkUpTypeTag = new TagType("Check-up");
        Tag firstVisitTag = new TagType("First visit");
        tagDao.insert(checkUpTypeTag);
        tagDao.insert(firstVisitTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, checkUpTypeTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, firstVisitTag);

        Search checkUpTagSearch  = new DecoratorSearchType(new SearchConcrete(), "Check-up");
        List<MedicalExam> checkUpExams = stateController.search(checkUpTagSearch);
        assertEquals(checkUpExams.size(), 1);

        Search firstVisitExamSearch = new DecoratorSearchType(new SearchConcrete(), "First visit");
        List<MedicalExam> firstVisitExams = stateController.search(firstVisitExamSearch);
        assertEquals(firstVisitExams.size(), 1);
    }

    @Test
    void searchByZoneTag() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = new Customer("Luca", "Verdi", "1990-05-04", 1, 2000);
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Visita", "Chek-up", "2025-10-01 15:30:00", "2025-10-01 16:30:00", 1000));
        addedMedicalExams.add(stateController.addMedicalExam(doctor.getId(), customer.getId(), "Titolo Seconda Visita", "Dieta", "2025-10-01 16:30:00", "2025-10-01 17:30:00", 1000));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag milanZoneTag = new TagZone("Milan");
        Tag florenceZoneTag = new TagZone("Florence");
        tagDao.insert(milanZoneTag);
        tagDao.insert(florenceZoneTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, milanZoneTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, florenceZoneTag);

        Search milanZoneSearch  = new DecoratorSearchZone(new SearchConcrete(), "Milan");
        List<MedicalExam> milanExams = stateController.search(milanZoneSearch);
        assertEquals(milanExams.size(), 1);

        Search florenceZoneSearch = new DecoratorSearchZone(new SearchConcrete(), "Florence");
        List<MedicalExam> florenceExams = stateController.search(florenceZoneSearch);
        assertEquals(florenceExams.size(), 1);
    }*/

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
    }
}
