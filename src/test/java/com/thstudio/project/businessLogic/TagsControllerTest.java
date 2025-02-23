package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Tags.Tag;
import com.thstudio.project.domainModel.Tags.TagIsOnline;
import com.thstudio.project.domainModel.Tags.TagType;
import com.thstudio.project.domainModel.Tags.TagZone;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TagsControllerTest {
    private static TagsController tagsController;
    private static TagDao tagDao;
    private static MedicalExamController medicalExamController;
    private static MedicalExamDao medicalExamDao;
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
        tagDao = new MariaDbTagDao();
        medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        tagsController = new TagsController(tagDao, medicalExamDao);
        NotificationDao notificationDao = new MariaDbNotificationDao();
        PersonDao personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
        tagDao = new MariaDbTagDao();
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
    void createNonExistentTag() {
        assertThrows(IllegalArgumentException.class, () -> tagsController.createTag("NonExistent", "NonExistent"));
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
        Doctor firstDoctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(firstDoctor.getId(), 0, "TEST", "DDDD", "2021-10-10 13:20:00", "2021-10-10 15:33:00", 1);
        tagsController.createTag("Zone1", "Zone");

        assertTrue(tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));

    }

    @Test
    void detachTagToMedicalExam() throws Exception {
        Doctor firstDoctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(firstDoctor);
        assertNotEquals(firstDoctor.getId(), 0);
        MedicalExam addedExam = medicalExamController.addMedicalExam(firstDoctor.getId(), 0, "TEST", "DDDD", "2021-10-10 13:20:00", "2021-10-10 15:33:00", 1);
        tagsController.createTag("Zone1", "Zone");
        tagsController.attachTagToMedicalExam("Zone1", "Zone", addedExam.getId());

        assertTrue(tagsController.detachTagToMedicalExam("Zone1", "Zone", addedExam.getId()));
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from tags").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
        connection.prepareStatement("delete from people").executeUpdate();
    }
}
