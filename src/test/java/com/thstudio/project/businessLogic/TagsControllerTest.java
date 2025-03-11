package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.MedicalExamFixture;
import com.thstudio.project.fixture.PersonFixture;
import com.thstudio.project.security.LoginController;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

class TagsControllerTest {
    private static TagsController tagsController;
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static LoginController loginController;
    private static PersonDao personDao;

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
        TagDao tagDao = new MariaDbTagDao();
        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        tagsController = new TagsController(tagDao, medicalExamDao);
        NotificationDao notificationDao = new MariaDbNotificationDao();
        personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
        loginController = new LoginController(personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
    }

    @Test
    void createZoneTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Tag zoneTag = new TagZone("Zone1");
        Tag zoneTagToAdd = tagsController.createTag("Zone1", "Zone", token);
        assertNotNull(zoneTagToAdd);
        assertEquals(zoneTag, zoneTagToAdd);
    }

    @Test
    void createTypeTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Tag typeTag = new TagType("Type1");
        Tag typeTagToAdd = tagsController.createTag("Type1", "Type", token);
        assertNotNull(typeTagToAdd);
        assertEquals(typeTag, typeTagToAdd);
    }

    @Test
    void createOnlineTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Tag onlineTag = new TagIsOnline("Online1");
        Tag onlineTagToAdd = tagsController.createTag("Online1", "Online", token);
        assertNotNull(onlineTagToAdd);
        assertEquals(onlineTag, onlineTagToAdd);
    }

    @Test
    void createNonExistentTagType() throws Exception {
        String token = loginController.login("test@test.com", "test");
        IllegalArgumentException ex = assertThrowsExactly(IllegalArgumentException.class, () -> {
            tagsController.createTag("NonExistent", "NonExistent", token);
        });
        assertEquals("Invalid tag type", ex.getMessage());
    }

    @Test
    void createAlreadyExistingTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Zone1", "Zone", token);
        Exception ex = assertThrowsExactly(SQLIntegrityConstraintViolationException.class, () -> tagsController.createTag("Zone1", "Zone", token));
        assertTrue(ex.getMessage().matches(".*Duplicate entry 'Zone1-Zone' for key 'PRIMARY'"));
    }

    @Test
    void deleteZoneTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Zone1", "Zone", token);
        assertTrue(tagsController.deleteTag("Zone1", "Zone", token));
    }

    @Test
    void deleteTypeTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Type1", "Type", token);
        assertTrue(tagsController.deleteTag("Type1", "Type", token));
    }

    @Test
    void deleteOnlineTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Online1", "Online", token);
        assertTrue(tagsController.deleteTag("Online1", "Online", token));
    }

    @Test
    void deleteNonExistentTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        assertFalse(tagsController.deleteTag("NonExistent", "Type", token));
    }

    @Test
    void attachTagToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        tagsController.createTag("Zone1", "Zone", token);
        assertTrue(tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));

    }

    @Test
    void attachTagToNonExistentMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Zone1", "Zone", token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> {
            tagsController.attachTagToMedicalExam("Zone1", "Zone", 0, token);
        });
        assertEquals("The Medical Exam looked for in not present in the database", ex.getMessage());
    }

    @Test
    void attachNonExistentTagToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));
        assertEquals("The Tag looked for in not present in the database", ex.getMessage());
    }

    @Test
    void attachAlreadyAttachedTag() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        tagsController.createTag("Zone1", "Zone", token);
        tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));
        assertEquals(ex.getMessage(), "The tag is already present");
    }

    @Test
    void detachTagToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        tagsController.createTag("Zone1", "Zone", token);
        tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token);

        assertTrue(tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));
    }

    @Test
    void detachTagToNonExistentMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        tagsController.createTag("Zone1", "Zone", token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", 0, token));
        assertEquals("The Medical Exam looked for in not present in the database", ex.getMessage());
    }

    @Test
    void detachNonExistentTagToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));
        assertEquals("The Tag looked for in not present in the database", ex.getMessage());
    }

    @Test
    void detachNonAttachedTagToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor), token);
        tagsController.createTag("Zone1", "Zone", token);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId(), token));
        assertEquals("The tag has been already detached", ex.getMessage());
    }


    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from tags").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
