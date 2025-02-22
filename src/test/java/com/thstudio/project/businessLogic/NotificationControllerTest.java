package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.Notification;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
        PersonDao personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        notificationController = new NotificationController(notificationDao, personDao);
    }

    @Test
    void getNotificationByReceiver() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Notification notificationToInsert = new Notification("Titolo notifica", doctor.getId());
        notificationDao.insert(notificationToInsert);
        List<Notification> notificationList = notificationController.getNotificationsByReceiverId(doctor.getId());
        assertEquals(notificationList.getFirst(), notificationToInsert);
    }

    @Test
    void getMoreThanOneNotificationByReceiver() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Notification firstNotificationToAdd = new Notification("Titolo notifica", doctor.getId());
        Notification secondNotificationToAdd = new Notification("Titolo seconda notifica", doctor.getId());
        List<Notification> notificationsToAdd = new ArrayList<>();
        notificationsToAdd.add(firstNotificationToAdd);
        notificationsToAdd.add(secondNotificationToAdd);
        for (Notification notificationToAdd : notificationsToAdd) {
            notificationDao.insert(notificationToAdd);
        }
        List<Notification> addedNotifications = notificationController.getNotificationsByReceiverId(doctor.getId());
        assertEquals(notificationsToAdd.size(), addedNotifications.size());
        for (int i = 0; i < notificationsToAdd.size(); ++i) {
            assertEquals(notificationsToAdd.get(i), addedNotifications.get(i));
        }
    }

    @Test
    void getNoNotifications() throws Exception {
        Doctor doctor = new Doctor("Marco", "Rossi", "2000-10-01", "MLN-01212", 12000);
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    notificationController.getNotificationsByReceiverId(doctor.getId());
                }
        );
        assertEquals(thrown.getMessage(), "The receiver has not notifications");
    }

    @Test
    void getNotificationsForAnUnknownReceiver() throws Exception {
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    notificationController.getNotificationsByReceiverId(1);
                }
        );
        assertEquals(thrown.getMessage(), "The person looked for in not present in the database");
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
    }
}
