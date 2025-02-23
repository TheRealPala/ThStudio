package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.Customer;
import com.thstudio.project.domainModel.Doctor;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.domainModel.State.Completed;
import com.thstudio.project.domainModel.State.Deleted;
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

import static org.junit.jupiter.api.Assertions.*;

class StateControllerTest {
    private static StateController stateController;
    private static MedicalExamController medicalExamController;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
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

        MedicalExamDao medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        PersonDao personDao = new MariaDbPersonDao();
        customerDao = new MariaDbCustomerDao(personDao);
        doctorDao = new MariaDbDoctorDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        stateController = new StateController(medicalExamDao, customerDao, doctorDao, notificationDao);
        medicalExamController = new MedicalExamController(medicalExamDao, notificationDao, doctorDao);
    }

    @Test
    void bookMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        MedicalExam bookedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
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
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId());
                }
        );
        assertEquals(thrown.getMessage(), "The exam you want to book is already booked");
    }

    @Test
    void bookTooExpensiveMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, customer.getBalance() + 1000));
        assertNotEquals(addedMedicalExam.getId(), 0);

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId());
                }
        );
        assertEquals(thrown.getMessage(), "not enough money");
    }

    @Test
    void cancelMedicalExamBooking() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        assertTrue(stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId()));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
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
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        Customer otherCustomer = CustomerFixture.genCustomer();
        customerDao.insert(otherCustomer);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), otherCustomer.getId());
                }
        );
        assertEquals(thrown.getMessage(), "Unauthorized request");
    }

    @Test
    void cancelNotBookedMedicalExamBooking() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId());
                }
        );
        assertEquals(thrown.getMessage(), "Can't cancel a booking for an exam which is not booked");
    }

    @Test
    void cancelAlreadyStartedMedicalExamBooking() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        MedicalExam bookedExam = medicalExamController.getExam(addedMedicalExam.getId());
        bookedExam.setStartTime(LocalDateTime.now());
        bookedExam.setEndTime(LocalDateTime.now().plusHours(1));
        medicalExamController.updateMedicalExam(bookedExam);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExamBooking(addedMedicalExam.getId(), customer.getId());
                }
        );
        assertEquals(thrown.getMessage(), "Can't cancel an exam already started");
    }

    @Test
    void cancelUnbookedMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId()));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
        assertEquals(deletedMedicalExam.getIdCustomer(), 0);
        assertInstanceOf(Deleted.class, deletedMedicalExam.getState());
    }

    @Test
    void cancelBookedMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId()));
        MedicalExam deletedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
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
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(1, doctor.getId());
                }
        );
        assertEquals(thrown.getMessage(), "The Medical Exam looked for in not present in the database");
    }

    @Test
    void attemptToCancelAMedicalExamFromACustomer() throws Exception {
        Doctor doctor =DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), customer.getId()));
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");
    }

    @Test
    void attemptToCancelAMedicalExamFromAStranger() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    assertTrue(stateController.cancelMedicalExam(addedMedicalExam.getId(), 0));
                }
        );
        assertEquals(thrown.getMessage(), "The Doctor looked for in not present in the database");
    }

    @Test
    void unauthorizedDeleteAttemptOfMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        Doctor otherDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(otherDoctor);
        assertNotEquals(otherDoctor.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(addedMedicalExam.getId(), otherDoctor.getId());
                }
        );
        assertEquals(thrown.getMessage(), "Unauthorized request");
    }

    @Test
    void cancelAlreadyStartedMedicalExam() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        addedMedicalExam.setStartTime(LocalDateTime.now());
        addedMedicalExam.setEndTime(LocalDateTime.now().plusHours(1));
        medicalExamController.updateMedicalExam(addedMedicalExam);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.cancelMedicalExam(addedMedicalExam.getId(), doctor.getId());
                }
        );
        assertEquals(thrown.getMessage(), "Can't cancel an exam already started");
    }

    @Test
    void markMedicalExamAsComplete() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        stateController.markMedicalExamAsComplete(addedMedicalExam.getId());
        MedicalExam completedMedicalExam = medicalExamController.getExam(addedMedicalExam.getId());
        assertInstanceOf(Completed.class, completedMedicalExam.getState());
    }

    @Test
    void markUnbookedMedicalExamAsComplete() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer, LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9)));
        assertNotEquals(addedMedicalExam.getId(), 0);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId());;
                }
        );
        assertEquals(thrown.getMessage(), "Can't mark an exam as complete if is not in booked state");
    }

    @Test
    void markUnfinishedMedicalExamAsComplete() throws Exception {
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        assertNotEquals(doctor.getId(), 0);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        assertNotEquals(customer.getId(), 0);
        MedicalExam addedMedicalExam = medicalExamController.addMedicalExam(MedicalExamFixture.genMedicalExam(doctor, customer));
        assertNotEquals(addedMedicalExam.getId(), 0);
        assertTrue(stateController.bookMedicalExam(addedMedicalExam.getId(), customer.getId()));
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    stateController.markMedicalExamAsComplete(addedMedicalExam.getId());;
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
