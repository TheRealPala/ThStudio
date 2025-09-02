package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import com.thstudio.project.domainModel.Search.*;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import com.thstudio.project.fixture.CustomerFixture;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.MedicalExamFixture;
import com.thstudio.project.fixture.PersonFixture;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MedicalExamControllerTest {
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
    private static TagDao tagDao;
    private static PersonDao personDao;
    private static LoginController loginController;

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
        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        NotificationDao notificationDao = new MariaDbNotificationDao();
        personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        customerDao = new MariaDbCustomerDao(personDao);
        tagDao = new MariaDbTagDao();
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
        loginController = new LoginController(personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
        personDao.setAdmin(person, true);
    }

    @Test
    void getAllMedicalExams() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor firstDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(firstDoctor);
        assertNotEquals(0, firstDoctor.getId());
        Doctor secondDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(secondDoctor);
        assertNotEquals(0, secondDoctor.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(secondDoctor), token));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(0, addedMedicalExam);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getAll(token).size());
    }

    @Test
    void getDoctorMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor firstDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(firstDoctor);
        assertNotEquals(0, firstDoctor.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor), token));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(0, addedMedicalExam);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getDoctorExams(firstDoctor.getId(), token).size());
    }

    @Test
    void getCustomerMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(0, addedMedicalExam);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getCustomerExams(customer.getId(), token).size());
    }

    @Test
    void addMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        MedicalExam medicalExamToAdd = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd), token);
        assertNotEquals(0, medicalExamToAdd.getId());
        MedicalExam medicalExamAdded = medicalExamController.getExam(medicalExamToAdd.getId(), token);
        assertEquals(medicalExamAdded, medicalExamToAdd);
    }

    @Test
    void addMedicalExamOfANonExistingDoctor() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
                }
        );
        assertEquals("The Doctor looked for in not present in the database", thrown.getMessage());

    }

    @Test
    void addMedicalExamWhenTheDoctorIsBusy() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2000-10-01 15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2000-10-01 16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), token);
        assertNotEquals(0, firstMedicalExam);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2000-10-01 15:45:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2000-10-01 17:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), token);
                }
        );
        assertEquals("The given doctor is already occupied in the given time range", thrown.getMessage());
    }

    @Test
    void updateMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        MedicalExam medicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd), token);
        assertNotEquals(0, medicalExam.getId());

        medicalExam.setTitle("Titolo Modificato");
        medicalExam.setDescription("Anamnesi dopo primo check");
        medicalExam.setStartTimeFromString("2025-10-01 16:30:00");
        medicalExam.setEndTimeFromString("2025-10-01 17:30:00");
        medicalExam.setPrice(2000);
        boolean outcomeUpdate = medicalExamController.updateMedicalExam(medicalExam, token);
        assertTrue(outcomeUpdate);
    }

    @Test
    void updateMedicalExamWhenDoctorIsBusy() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2025-10-01 15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2025-10-01 16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))), token);
        assertNotEquals(0, firstMedicalExam.getId());
        MedicalExam secondMedicalExam = medicalExamController.addMedicalExam((MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2025-10-01 17:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2025-10-01 17:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))), token);
        secondMedicalExam.setStartTimeFromString("2025-10-01 16:00:00");
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.updateMedicalExam(secondMedicalExam, token);
                }
        );
        assertEquals("The given doctor is already occupied in the given time range", thrown.getMessage());
    }

    @Test
    void updateMedicalExamAfterStartTime() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(0, doctorToAdd.getId());
        LocalDateTime now = LocalDateTime.now();
        MedicalExam medicalExamToAdd = MedicalExamFixture.genMedicalExam(doctorToAdd);
        medicalExamToAdd.setStartTime(now);
        medicalExamToAdd.setEndTime(now.plusHours(5));
        MedicalExam medicalExamAdded = medicalExamController.addMedicalExam(medicalExamToAdd, token);

        medicalExamAdded.setTitle("Titolo Modificato");
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.updateMedicalExam(medicalExamAdded, token);
                }
        );
        assertEquals("Forbidden! Can't update an exam already started", thrown.getMessage());
    }

    @Test
    void getMedicalExamByState() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(0, addedMedicalExam.getId());
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Available(), token).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            medicalExamController.updateMedicalExam(addedMedicalExam, token);
        }

        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Booked(now), token).size());
    }

    @Test
    void searchByOnlineTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag isOnlineTag = new TagIsOnline("yes");
        Tag inPersonTag = new TagIsOnline("no");
        tagDao.insert(isOnlineTag);
        tagDao.insert(inPersonTag);
        tagDao.attachTagToMedicalExam(firstMedicalExam, isOnlineTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, inPersonTag);

        Search onlineExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "yes");
        List<MedicalExam> onlineExams = medicalExamController.search(onlineExamSearch, token);
        assertEquals(1, onlineExams.size());

        Search inPersonExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "no");
        List<MedicalExam> inPersonExams = medicalExamController.search(inPersonExamSearch, token);
        assertEquals(1, inPersonExams.size());
    }

    @Test
    void searchByPrice() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, 1000), token);
        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, 5000), token);

        Search priceBelowOrEqualToOneThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 1000);
        List<MedicalExam> examsBelowOneThousand = medicalExamController.search(priceBelowOrEqualToOneThousandSearch, token);
        assertEquals(1, examsBelowOneThousand.size());

        Search priceBelowOrEqualToFiveThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 5000);
        List<MedicalExam> examsBelowFiveThousand = medicalExamController.search(priceBelowOrEqualToFiveThousandSearch, token);
        assertEquals(2, examsBelowFiveThousand.size());
    }

    @Test
    void searchByStartTime() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.parse("2025-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1), token);
        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.parse("2025-02-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1), token);

        Search startAfterOrInJanuarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-01-01 00:00:00");
        List<MedicalExam> examsAfterOrInJanuary = medicalExamController.search(startAfterOrInJanuarySearch, token);
        assertEquals(2, examsAfterOrInJanuary.size());

        Search startAfterOrInFebruarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-02-01 00:00:00");
        List<MedicalExam> examsAfterOrInFebruary = medicalExamController.search(startAfterOrInFebruarySearch, token);
        assertEquals(1, examsAfterOrInFebruary.size());

    }

    @Test
    void searchByState() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(0, addedMedicalExam);
        }
        Search examsAvailableSearch = new DecoratorSearchState(new SearchConcrete(), new Available().getState());
        assertEquals(addedMedicalExams.size(), medicalExamController.search(examsAvailableSearch, token).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            medicalExamController.updateMedicalExam(addedMedicalExam, token);
        }

        Search examsBookedSearch = new DecoratorSearchState(new SearchConcrete(), new Booked(now).getState());
        assertEquals(addedMedicalExams.size(), medicalExamController.search(examsBookedSearch, token).size());
    }

    @Test
    void searchByTypeTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag checkUpTypeTag = new TagType("Check-up");
        Tag firstVisitTag = new TagType("First visit");
        tagDao.insert(checkUpTypeTag);
        tagDao.insert(firstVisitTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, checkUpTypeTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, firstVisitTag);

        Search checkUpTagSearch = new DecoratorSearchType(new SearchConcrete(), "Check-up");
        List<MedicalExam> checkUpExams = medicalExamController.search(checkUpTagSearch, token);
        assertEquals(1, checkUpExams.size());

        Search firstVisitExamSearch = new DecoratorSearchType(new SearchConcrete(), "First visit");
        List<MedicalExam> firstVisitExams = medicalExamController.search(firstVisitExamSearch, token);
        assertEquals(1, firstVisitExams.size());
    }

    @Test
    void searchByZoneTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag milanZoneTag = new TagZone("Milan");
        Tag florenceZoneTag = new TagZone("Florence");
        tagDao.insert(milanZoneTag);
        tagDao.insert(florenceZoneTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, milanZoneTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, florenceZoneTag);

        Search milanZoneSearch = new DecoratorSearchZone(new SearchConcrete(), "Milan");
        List<MedicalExam> milanExams = medicalExamController.search(milanZoneSearch, token);
        assertEquals(1, milanExams.size());

        Search florenceZoneSearch = new DecoratorSearchZone(new SearchConcrete(), "Florence");
        List<MedicalExam> florenceExams = medicalExamController.search(florenceZoneSearch, token);
        assertEquals(1, florenceExams.size());
    }

    @Test
    void runMethodsWithoutRequiredRole() throws Exception {
        Person personAdded = personDao.getPersonByUsername("test@test.com");
        personDao.setAdmin(personAdded, false);
        String token = loginController.login("test@test.com", "test");
        SecurityException excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(DoctorFixture.genDoctor()), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.getAll(token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.getExamsByState(new Available(), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.getDoctorExams(1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.getCustomerExams(1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.getExam(1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.updateMedicalExam(MedicalExamFixture.genMedicalExam(DoctorFixture.genDoctor()),
                        token));
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.search(new SearchConcrete(), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(DoctorFixture.genDoctor()), token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
        connection.prepareStatement("delete from tags").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
    }
}
