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
import com.thstudio.project.security.LoginController;
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
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
        loginController = new LoginController(personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
    }

    @Test
    void bookMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        MedicalExam bookedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertInstanceOf(Booked.class, bookedMedicalExam.getState());
        assertEquals(bookedMedicalExam.getIdCustomer(), customer.getId());
        //check of payment
        Customer customerAfterPayment = customerDao.get(customer.getId());
        Doctor doctorAfterPayment = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance() - bookedMedicalExam.getPrice(), customerAfterPayment.getBalance());
        assertEquals(doctor.getBalance() + bookedMedicalExam.getPrice(), doctorAfterPayment.getBalance());
        //check gen of notification
        Notification bookedNotification = notificationDao.getNotificationsByReceiverId(doctor.getId()).getFirst();
        assertEquals(bookedNotification.getTitle(), "Booked exam " + bookedMedicalExam.getTitle() + " by:" + customer.getName());
    }

    @Test
    void bookMedicalExamAlreadyBooked() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "The exam you want to book is already booked");
    }

    @Test
    void bookTooExpensiveMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, customer.getBalance() + 1000), token);
        assertNotEquals(addedMedicalExam.getId(), 0);

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "not enough money");
    }

    @Test
    void cancelMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        assertTrue(stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(deletedMedicalExam.getIdCustomer(), 0);
        assertInstanceOf(Available.class, deletedMedicalExam.getState());
        //check of payment
        Customer customerAfterRefund = customerDao.get(customer.getId());
        Doctor doctorAfterRefund = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance(), customerAfterRefund.getBalance());
        assertEquals(doctor.getBalance(), doctorAfterRefund.getBalance());
        //check gen of notification
        Notification deletedBookingNotification = notificationDao.getNotificationsByReceiverId(doctor.getId()).get(1);
        assertEquals(deletedBookingNotification.getTitle(), "Deleted exam booking " + deletedMedicalExam.getTitle() + " by:" + customer.getName());
    }

    @Test
    void cancelMedicalExamBookingOfADifferentCustomer() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        Customer otherCustomer = CustomerFixture.genCustomer();
        customerDao.insert(otherCustomer);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), otherCustomer.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "Unauthorized request");
    }

    @Test
    void cancelNotBookedMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "Can't cancel a booking for an exam which is not booked");
    }

    @Test
    void cancelAlreadyStartedMedicalExamBooking() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
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
        assertEquals(thrown.getMessage(), "Can't cancel an exam already started");
    }

    @Test
    void cancelUnbookedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(deletedMedicalExam.getIdCustomer(), 0);
        assertInstanceOf(Deleted.class, deletedMedicalExam.getState());
    }

    @Test
    void cancelBookedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId(), token));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId(), token);
        assertEquals(deletedMedicalExam.getIdCustomer(), 0);
        assertInstanceOf(Deleted.class, deletedMedicalExam.getState());
        //check of refund
        Customer customerAfterRefund = customerDao.get(customer.getId());
        Doctor doctorAfterRefund = doctorDao.get(doctor.getId());
        assertEquals(customer.getBalance(), customerAfterRefund.getBalance());
        assertEquals(doctor.getBalance(), doctorAfterRefund.getBalance());
        //check gen of notification
        Notification deletedBookingNotification = notificationDao.getNotificationsByReceiverId(customer.getId()).getFirst();
        assertEquals(deletedBookingNotification.getTitle(), "Deleted exam " + deletedMedicalExam.getTitle() + " by:" + doctor.getName());
    }

    @Test
    void cancelUnknownMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(1, doctor.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "The Medical Exam looked for in not present in the database");
    }

    @Test
    void attemptToCancelAMedicalExamFromACustomer() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");
    }

    @Test
    void attemptToCancelAMedicalExamFromAStranger() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), 0, token));
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");
    }

    @Test
    void unauthorizedDeleteAttemptOfMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        Doctor otherDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(otherDoctor);
        assertNotEquals(otherDoctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(addedMedicalExam.getId(), otherDoctor.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "Unauthorized request");
    }

    @Test
    void cancelAlreadyStartedMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        addedMedicalExam.setStartTime(LocalDateTime.now());
        addedMedicalExam.setEndTime(LocalDateTime.now().plusHours(1));
        medicalExamController.updateMedicalExam(addedMedicalExam, token);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId(), token);
                }
        );
        assertEquals(thrown.getMessage(), "Can't cancel an exam already started");
    }

    @Test
    void markMedicalExamAsComplete() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
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
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId(), token);
                    ;
                }
        );
        assertEquals(thrown.getMessage(), "Can't mark an exam as complete if is not in booked state");
    }

    @Test
    void markUnfinishedMedicalExamAsComplete() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer), token);
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId(), token));
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId(), token);
                    ;
                }
        );
        assertEquals(thrown.getMessage(), "Can't mark an exam as complete if is not finished");
    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
    }
}
