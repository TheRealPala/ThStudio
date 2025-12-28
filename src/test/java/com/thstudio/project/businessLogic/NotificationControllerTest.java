package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.fixture.DoctorFixture;
import com.thstudio.project.fixture.NotificationFixture;
import com.thstudio.project.fixture.PersonFixture;
import com.thstudio.project.security.Authn;
import com.thstudio.project.security.Authz;
import com.thstudio.project.security.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationControllerTest {
    private static NotificationController notificationController;
    private static DoctorDao doctorDao;
    private static NotificationDao notificationDao;
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
        JwtService jwtService = new JwtService();
        Authz authz = new Authz(jwtService);
        Authn authn = new Authn(jwtService);
        personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        notificationController = new NotificationController(notificationDao, personDao, authz);
        loginController = new LoginController(personDao, authn);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
        personDao.setAdmin(person, true);
    }

    @Test
    void getNotificationByReceiver() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Notification notificationToInsert = new Notification(NotificationFixture.genTitle(), doctor.getId());
        notificationDao.insert(notificationToInsert);
        List<Notification> notificationList = notificationController.getNotificationsByReceiverId(doctor.getId(), token);
        assertEquals(notificationList.getFirst(), notificationToInsert);
    }

    @Test
    void getMoreThanOneNotificationByReceiver() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Notification firstNotificationToAdd = new Notification(NotificationFixture.genTitle(), doctor.getId());
        Notification secondNotificationToAdd = new Notification(NotificationFixture.genTitle(), doctor.getId());
        List<Notification> notificationsToAdd = new ArrayList<>();
        notificationsToAdd.add(firstNotificationToAdd);
        notificationsToAdd.add(secondNotificationToAdd);
        for (Notification notificationToAdd : notificationsToAdd) {
            notificationDao.insert(notificationToAdd);
        }
        List<Notification> addedNotifications = notificationController.getNotificationsByReceiverId(doctor.getId(), token);
        assertEquals(notificationsToAdd.size(), addedNotifications.size());
        for (int i = 0; i < notificationsToAdd.size(); ++i) {
            assertEquals(notificationsToAdd.get(i), addedNotifications.get(i));
        }
    }

    @Test
    void getNoNotifications() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    notificationController.getNotificationsByReceiverId(doctor.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "The receiver has not notifications");
    }

    @Test
    void getNotificationsForAnUnknownReceiver() throws Exception {
    String token = loginController.login("test@test.com", "test");
    RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    notificationController.getNotificationsByReceiverId(1, token);
                }
        );
        assertEquals(thrown.getMessage(), "The person looked for in not present in the database");
    }

    @Test
    void runMethodsWithoutRequiredRole() throws Exception {
        Person personAdded = personDao.getPersonByUsername("test@test.com");
        personDao.setAdmin(personAdded, false);
        String token = loginController.login("test@test.com", "test");
        SecurityException excp = assertThrowsExactly(
                SecurityException.class,
                () -> notificationController.getNotificationsByReceiverId(1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
    }
    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
    }
}
