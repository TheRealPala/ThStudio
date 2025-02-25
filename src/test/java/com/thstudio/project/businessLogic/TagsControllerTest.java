package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.MedicalExamFixture;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
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
        TagDao tagDao = new MariaDbTagDao();
        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        tagsController = new TagsController(tagDao, medicalExamDao);
        NotificationDao notificationDao = new MariaDbNotificationDao();
        PersonDao personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
    }

    @Test
    void createZoneTag() throws Exception {
        Tag zoneTag = new TagZone("Zone1");
        Tag zoneTagToAdd = tagsController.createTag("Zone1", "Zone");
        assertNotNull(zoneTagToAdd);
        assertEquals(zoneTag, zoneTagToAdd);
    }

    @Test
    void createTypeTag() throws Exception {
        Tag typeTag = new TagType("Type1");
        Tag typeTagToAdd = tagsController.createTag("Type1", "Type");
        assertNotNull(typeTagToAdd);
        assertEquals(typeTag, typeTagToAdd);
    }

    @Test
    void createOnlineTag() throws Exception {
        Tag onlineTag = new TagIsOnline("Online1");
        Tag onlineTagToAdd = tagsController.createTag("Online1", "Online");
        assertNotNull(onlineTagToAdd);
        assertEquals(onlineTag, onlineTagToAdd);
    }

    @Test
    void createNonExistentTagType() {
        IllegalArgumentException ex = assertThrowsExactly(IllegalArgumentException.class, () -> {
            tagsController.createTag("NonExistent", "NonExistent");
        });
        assertEquals("Invalid tag type", ex.getMessage());
    }

    @Test
    void createAlreadyExistingTag() throws Exception {
        tagsController.createTag("Zone1", "Zone");
        Exception ex = assertThrowsExactly(SQLIntegrityConstraintViolationException.class, () -> tagsController.createTag("Zone1", "Zone"));
        assertTrue(ex.getMessage().matches(".*Duplicate entry 'Zone1-Zone' for key 'PRIMARY'"));
    }

    @Test
    void deleteZoneTag() throws Exception {
        tagsController.createTag("Zone1", "Zone");
        assertTrue(tagsController.deleteTag("Zone1", "Zone"));
    }

    @Test
    void deleteTypeTag() throws Exception {
        tagsController.createTag("Type1", "Type");
        assertTrue(tagsController.deleteTag("Type1", "Type"));
    }

    @Test
    void deleteOnlineTag() throws Exception {
        tagsController.createTag("Online1", "Online");
        assertTrue(tagsController.deleteTag("Online1", "Online"));
    }

    @Test
    void deleteNonExistentTag() throws Exception {
        assertFalse(tagsController.deleteTag("NonExistent", "Type"));
    }

    @Test
    void attachTagToMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        tagsController.createTag("Zone1", "Zone");
        assertTrue(tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));

    }

    @Test
    void attachTagToNonExistentMedicalExam() throws Exception {
        tagsController.createTag("Zone1", "Zone");
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> {
            tagsController.attachTagToMedicalExam("Zone1", "Zone", 0);
        });
        assertEquals("The Medical Exam looked for in not present in the database", ex.getMessage());
    }

    @Test
    void attachNonExistentTagToMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
        assertEquals("The Tag looked for in not present in the database", ex.getMessage());
    }

    @Test
    void attachAlreadyAttachedTag() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        tagsController.createTag("Zone1", "Zone");
        tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId());
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
        assertEquals(ex.getMessage(), "The tag is already present");
    }

    @Test
    void detachTagToMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        tagsController.createTag("Zone1", "Zone");
        tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId());

        assertTrue(tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
    }

    @Test
    void detachTagToNonExistentMedicalExam() throws Exception {
        tagsController.createTag("Zone1", "Zone");
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", 0));
        assertEquals("The Medical Exam looked for in not present in the database", ex.getMessage());
    }

    @Test
    void detachNonExistentTagToMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
        assertEquals("The Tag looked for in not present in the database", ex.getMessage());
    }

    @Test
    void detachNonAttachedTagToMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor));
        tagsController.createTag("Zone1", "Zone");
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
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
