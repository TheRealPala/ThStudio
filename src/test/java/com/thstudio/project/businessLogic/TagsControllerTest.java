package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.businessLogic.TagsController;
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
    private static MedicalExamDao medicalExamDao;

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
        tagsController = new TagsController(tagDao, medicalExamDao);
    }

    @Test
    void createZoneTag() throws Exception {
        Tag zoneTag = new TagZone("Zone1");
        Tag zoneTagToAdd = tagsController.createTag("Zone1", "Zone");
        assertNotNull(zoneTagToAdd);
        assertEquals(zoneTag, zoneTagToAdd);
    }

    @Test
    void createOnlineTag() throws Exception {
        Tag onlineTag = new TagIsOnline("Online1");
        Tag onlineTagToAdd = tagsController.createTag("Online1", "Online");
        assertNotNull(onlineTagToAdd);
        assertEquals(onlineTag, onlineTagToAdd);
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from tags").executeUpdate();
    }
}
