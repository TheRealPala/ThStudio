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

class MedicalExamControllerTest {
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
    private static TagDao tagDao;

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
        tagDao = new MariaDbTagDao();
    }

    @Test
    void getAllMedicalExams() throws Exception {
        Doctor firstDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);
        Doctor secondDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(secondDoctor);
        assertNotEquals(secondDoctor.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(secondDoctor)));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getAll().size());
    }

    @Test
    void getDoctorMedicalExam() throws Exception {
        Doctor firstDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(firstDoctor)));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getDoctorExams(firstDoctor.getId()).size());
    }

    @Test
    void getCustomerMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getCustomerExams(customer.getId()).size());
    }

    @Test
    void addMedicalExam() throws Exception {
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExamToAdd = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd));
        assertNotEquals(medicalExamToAdd.getId(), 0);
        MedicalExam medicalExamAdded = medicalExamController.getExam(medicalExamToAdd.getId());
        assertEquals(medicalExamAdded, medicalExamToAdd);
    }

    @Test
    void addMedicalExamOfANonExistingDoctor() {
        Doctor doctor = DoctorFixture.genDoctor();
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");

    }

    @Test
    void addMedicalExamWhenTheDoctorIsBusy() throws Exception {
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2000-10-01 15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2000-10-01 16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        assertNotEquals(firstMedicalExam, 0);
        IllegalArgumentException thrown = assertThrowsExactly(IllegalArgumentException.class,
                () -> {
                    medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2000-10-01 15:45:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2000-10-01 17:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
                }
        );
        assertEquals(thrown.getMessage(), "The given doctor is already occupied in the given time range");
    }

    @Test
    void updateMedicalExam() throws Exception {
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam medicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd));
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
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        MedicalExam firstMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2025-10-01 15:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2025-10-01 16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        assertNotEquals(firstMedicalExam.getId(), 0);
        MedicalExam secondMedicalExam = medicalExamController.addMedicalExam((MedicalExamFixture.genMedicalExam(doctorToAdd, LocalDateTime.parse("2025-10-01 17:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), LocalDateTime.parse("2025-10-01 17:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
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
        Doctor doctorToAdd = DoctorFixture.genDoctor();
        doctorDao.insert(doctorToAdd);
        assertNotEquals(doctorToAdd.getId(), 0);
        LocalDateTime now = LocalDateTime.now();
        MedicalExam medicalExamToAdd = MedicalExamFixture.genMedicalExam(doctorToAdd);
        medicalExamToAdd.setStartTime(now);
        medicalExamToAdd.setEndTime(now.plusHours(5));
        MedicalExam medicalExamAdded = medicalExamController.addMedicalExam(medicalExamToAdd);

        medicalExamAdded.setTitle("Titolo Modificato");
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.updateMedicalExam(medicalExamAdded);
                }
        );
        assertEquals(thrown.getMessage(), "Forbidden! Can't update an exam already started");
    }

    @Test
    void getMedicalExamByState() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam.getId(), 0);
        }
        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Available()).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            medicalExamController.updateMedicalExam(addedMedicalExam);
        }

        assertEquals(addedMedicalExams.size(), medicalExamController.getExamsByState(new Booked(now)).size());
    }

    @Test
    void searchByOnlineTag() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag isOnlineTag = new TagIsOnline("yes");
        Tag inPersonTag = new TagIsOnline("no");
        tagDao.insert(isOnlineTag);
        tagDao.insert(inPersonTag);
        tagDao.attachTagToMedicalExam(firstMedicalExam, isOnlineTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, inPersonTag);

        Search onlineExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "yes");
        List<MedicalExam> onlineExams = medicalExamController.search(onlineExamSearch);
        assertEquals(onlineExams.size(), 1);

        Search inPersonExamSearch = new DecoratorSearchIsOnline(new SearchConcrete(), "no");
        List<MedicalExam> inPersonExams = medicalExamController.search(inPersonExamSearch);
        assertEquals(inPersonExams.size(), 1);
    }

    @Test
    void searchByPrice() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, 1000));
        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, 5000));

        Search priceBelowOrEqualToOneThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 1000);
        List<MedicalExam> examsBelowOneThousand = medicalExamController.search(priceBelowOrEqualToOneThousandSearch);
        assertEquals(examsBelowOneThousand.size(), 1);

        Search priceBelowOrEqualToFiveThousandSearch = new DecoratorSearchPrice(new SearchConcrete(), 5000);
        List<MedicalExam> examsBelowFiveThousand = medicalExamController.search(priceBelowOrEqualToFiveThousandSearch);
        assertEquals(examsBelowFiveThousand.size(), 2);
    }

    @Test
    void searchByStartTime() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.parse("2025-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1));
        medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.parse("2025-02-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 1));

        Search startAfterOrInJanuarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-01-01 00:00:00");
        List<MedicalExam> examsAfterOrInJanuary = medicalExamController.search(startAfterOrInJanuarySearch);
        assertEquals(examsAfterOrInJanuary.size(), 2);

        Search startAfterOrInFebruarySearch = new DecoratorSearchStartTime(new SearchConcrete(), "2025-02-01 00:00:00");
        List<MedicalExam> examsAfterOrInFebruary = medicalExamController.search(startAfterOrInFebruarySearch);
        assertEquals(examsAfterOrInFebruary.size(), 1);

    }

    @Test
    void searchByState() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            assertNotEquals(addedMedicalExam, 0);
        }
        Search examsAvailableSearch = new DecoratorSearchState(new SearchConcrete(), new Available().getState());
        assertEquals(addedMedicalExams.size(), medicalExamController.search(examsAvailableSearch).size());

        LocalDateTime now = LocalDateTime.now();
        for (MedicalExam addedMedicalExam : addedMedicalExams) {
            addedMedicalExam.setState(new Booked(now));
            medicalExamController.updateMedicalExam(addedMedicalExam);
        }

        Search examsBookedSearch = new DecoratorSearchState(new SearchConcrete(), new Booked(now).getState());
        assertEquals(addedMedicalExams.size(), medicalExamController.search(examsBookedSearch).size());
    }

    @Test
    void searchByTypeTag() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag checkUpTypeTag = new TagType("Check-up");
        Tag firstVisitTag = new TagType("First visit");
        tagDao.insert(checkUpTypeTag);
        tagDao.insert(firstVisitTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, checkUpTypeTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, firstVisitTag);

        Search checkUpTagSearch = new DecoratorSearchType(new SearchConcrete(), "Check-up");
        List<MedicalExam> checkUpExams = medicalExamController.search(checkUpTagSearch);
        assertEquals(checkUpExams.size(), 1);

        Search firstVisitExamSearch = new DecoratorSearchType(new SearchConcrete(), "First visit");
        List<MedicalExam> firstVisitExams = medicalExamController.search(firstVisitExamSearch);
        assertEquals(firstVisitExams.size(), 1);
    }

    @Test
    void searchByZoneTag() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);

        ArrayList<MedicalExam> addedMedicalExams = new ArrayList<>();
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        addedMedicalExams.add(medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer)));
        MedicalExam firstMedicalExam = addedMedicalExams.getFirst();
        MedicalExam secondMedicalExam = addedMedicalExams.get(1);

        Tag milanZoneTag = new TagZone("Milan");
        Tag florenceZoneTag = new TagZone("Florence");
        tagDao.insert(milanZoneTag);
        tagDao.insert(florenceZoneTag);

        tagDao.attachTagToMedicalExam(firstMedicalExam, milanZoneTag);
        tagDao.attachTagToMedicalExam(secondMedicalExam, florenceZoneTag);

        Search milanZoneSearch = new DecoratorSearchZone(new SearchConcrete(), "Milan");
        List<MedicalExam> milanExams = medicalExamController.search(milanZoneSearch);
        assertEquals(milanExams.size(), 1);

        Search florenceZoneSearch = new DecoratorSearchZone(new SearchConcrete(), "Florence");
        List<MedicalExam> florenceExams = medicalExamController.search(florenceZoneSearch);
        assertEquals(florenceExams.size(), 1);
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
