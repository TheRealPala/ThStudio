package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.State.Completed;
import com.thstudio.project.domainModel.State.Deleted;
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

import static org.junit.jupiter.api.Assertions.*;

class StateControllerTest {
    private static StateController stateController;
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
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

        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        personDao = new MariaDbPersonDao();
        customerDao = new MariaDbCustomerDao(personDao);
        doctorDao = new MariaDbDoctorDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        stateController = new StateController(medicalExamDao, customerDao, doctorDao, notificationDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao, customerDao);
        loginController = new LoginController(personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
        personDao.setAdmin(person, true);
    }

    @Test
    void bookMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        MedicalExam bookedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertInstanceOf(Booked.class, bookedMedicalExam.getState());
        assertEquals(customer.getId(), bookedMedicalExam.getIdCustomer());
        //check of payment
        Customer customerAfterPayment = customerDao.get(customer.getId());
        Doctor doctorAfterPayment = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance() - bookedMedicalExam.getPrice(), customerAfterPayment.getBalance());
        assertEquals(doctor.getBalance() + bookedMedicalExam.getPrice(), doctorAfterPayment.getBalance());
        //check gen of notification
        Notification bookedNotification = notificationDao.getNotificationsByReceiverId(doctor.getId()).getFirst();
        assertEquals("Booked exam " + bookedMedicalExam.getTitle() + " by:" + customer.getName(), bookedNotification.getTitle());
    }

    @Test
    void bookMedicalExamAlreadyBooked() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals("The exam you want to book is already booked", thrown.getMessage());
    }

    @Test
    void bookTooExpensiveMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, customer.getBalance() + 1000), token);
        assertNotEquals(0, addedMedicalExam.getId());

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals("not enough money", thrown.getMessage());
    }

    @Test
    void cancelMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        assertTrue(stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(0, deletedMedicalExam.getIdCustomer());
        assertInstanceOf(Available.class, deletedMedicalExam.getState());
        //check of payment
        Customer customerAfterRefund = customerDao.get(customer.getId());
        Doctor doctorAfterRefund = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance(), customerAfterRefund.getBalance());
        assertEquals(doctor.getBalance(), doctorAfterRefund.getBalance());
        //check gen of notification
        Notification deletedBookingNotification = notificationDao.getNotificationsByReceiverId(doctor.getId()).get(1);
        assertEquals("Deleted exam booking " + deletedMedicalExam.getTitle() + " by:" + customer.getName(), deletedBookingNotification.getTitle());
    }

    @Test
    void cancelMedicalExamBookingOfADifferentCustomer() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        Customer otherCustomer = CustomerFixture.genCustomer();
        customerDao.insert(otherCustomer);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), otherCustomer.getId(), token);
                }
        );
        assertEquals("Unauthorized request", thrown.getMessage());
    }

    @Test
    void cancelNotBookedMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals("Can't cancel a booking for an exam which is not booked", thrown.getMessage());
    }

    @Test
    void cancelAlreadyStartedMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        MedicalExam bookedExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        bookedExam.setStartTime(LocalDateTime.now());
        bookedExam.setEndTime(LocalDateTime.now().plusHours(1));
        medicalExamController.updateMedicalExam(bookedExam, token);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals("Can't cancel an exam already started", thrown.getMessage());
    }

    @Test
    void cancelUnbookedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), doctor.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(0, deletedMedicalExam.getIdCustomer());
        assertInstanceOf(Deleted.class, deletedMedicalExam.getState());
    }

    @Test
    void cancelBookedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        assertTrue(medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), doctor.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(0, deletedMedicalExam.getIdCustomer());
        assertInstanceOf(Deleted.class, deletedMedicalExam.getState());
        //check of refund
        Customer customerAfterRefund = customerDao.get(customer.getId());
        Doctor doctorAfterRefund = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance(), customerAfterRefund.getBalance());
        assertEquals(doctor.getBalance(), doctorAfterRefund.getBalance());
        //check gen of notification
        Notification deletedBookingNotification = notificationDao.getNotificationsByReceiverId(customer.getId()).getFirst();
        assertEquals("Deleted exam " + deletedMedicalExam.getTitle() + " by:" + doctor.getName(), deletedBookingNotification.getTitle());
    }

    @Test
    void cancelUnknownMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.deleteMedicalExam(1, doctor.getId(), token);
                }
        );
        assertEquals("The Medical Exam looked for in not present in the database", thrown.getMessage());
    }

    @Test
    void attemptToCancelAMedicalExamFromACustomer() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
                }
        );
        assertEquals("The Doctor looked for in not present in the database", thrown.getMessage());
    }

    @Test
    void attemptToCancelAMedicalExamFromAStranger() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), 0, token));
                }
        );
        assertEquals("The Doctor looked for in not present in the database", thrown.getMessage());
    }

    @Test
    void unauthorizedDeleteAttemptOfMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        Doctor otherDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(otherDoctor);
        assertNotEquals(0, otherDoctor.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), otherDoctor.getId(), token);
                }
        );
        assertEquals("Unauthorized request", thrown.getMessage());
    }

    @Test
    void cancelAlreadyStartedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        addedMedicalExam.setStartTime(LocalDateTime.now());
        addedMedicalExam.setEndTime(LocalDateTime.now().plusHours(1));
        medicalExamController.updateMedicalExam(addedMedicalExam, token);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    medicalExamController.deleteMedicalExam(addedMedicalExam.getId(), doctor.getId(), token);
                }
        );
        assertEquals("Can't cancel an exam already started", thrown.getMessage());
    }

    @Test
    void markMedicalExamAsComplete() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        stateController.markMedicalExamAsComplete(addedMedicalExam.getId(), token);
        MedicalExam completedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertInstanceOf(Completed.class, completedMedicalExam.getState());
    }

    @Test
    void markUnbookedMedicalExamAsComplete() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)), token);
        assertNotEquals(0, addedMedicalExam.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId(), token);
                    ;
                }
        );
        assertEquals("Can't mark an exam as complete if is not in booked state", thrown.getMessage());
    }

    @Test
    void markUnfinishedMedicalExamAsComplete() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(0, doctor.getId());
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(0, customer.getId());
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(0, addedMedicalExam.getId());
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId(), token);
                    ;
                }
        );
        assertEquals("Can't mark an exam as complete if is not finished", thrown.getMessage());
    }

    @Test
    void runMethodsWithoutRequiredRole() throws Exception {
        Person doctorAdded = personDao.getPersonByUsername("test@test.com");
        personDao.setAdmin(doctorAdded, false);
        String token = loginController.login("test@test.com", "test");
        SecurityException excp = assertThrowsExactly(
                SecurityException.class,
                () -> stateController.bookMedicalExam(1, 1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> stateController.cancelMedicalExamBooking(1, 1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> medicalExamController.deleteMedicalExam(1, 1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> stateController.markMedicalExamAsComplete(1, token)
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
    }
}
