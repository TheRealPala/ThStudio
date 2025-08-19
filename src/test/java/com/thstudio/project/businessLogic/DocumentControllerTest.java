package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import com.thstudio.project.fixture.*;
import com.thstudio.project.security.LoginController;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class DocumentControllerTest {
    private static DocumentController documentController;
    private static LoginController loginController;
    private static DocumentDao documentDao;
    private static PersonDao personDao;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
    private static MedicalExamDao medicalExamDao;
    private static NotificationDao notificationDao;

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
        documentDao = new MariaDbDocumentDao();
        personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        customerDao = new MariaDbCustomerDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        documentController = new DocumentController(documentDao, personDao, notificationDao, medicalExamDao);
        loginController = new LoginController(personDao);
    }

    @BeforeEach
    void setUpTestUser() throws Exception {
        Person person = PersonFixture.genTestPerson();
        personDao.insert(person);
        personDao.setAdmin(person, true);
    }

    @Test
    void addDocument() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Person personToAdd = PersonFixture.genPerson();
        personDao.insert(personToAdd);
        assertNotEquals(0, personToAdd.getId());
        Document documentToAdd = documentController.addDocument(DocumentFixture.genTitle(), personToAdd.getId(), token);
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertEquals(documentToAdd, addedDocument);
    }

    @Test
    public void getDocumentsByReceiver() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument(DocumentFixture.genTitle(), doctor.getId(), token);
        documentToAdd.setReceiverId(customer.getId());
        documentDao.update(documentToAdd);
        List<Document> addedDocuments = documentController.getDocumentsByReceiver(customer.getId(), token);
        assertNotNull(addedDocuments);
        assertTrue(addedDocuments.contains(documentToAdd));
        assertEquals(documentToAdd, addedDocuments.getFirst());
        //controllo dell' eccezione
        Customer otherCustomer = CustomerFixture.genCustomer();
        customerDao.insert(otherCustomer);

        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.getDocumentsByReceiver(otherCustomer.getId(), token);
                }
        );
        assertEquals("There is no Documents in the database for this receiver", thrown.getMessage());

    }

    @Test
    public void getDocumentsByOwner() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        Document documentToAdd = documentController.addDocument(DocumentFixture.genTitle(), doctor.getId(), token);
        List<Document> addedDocuments = documentController.getDocumentsByOwner(doctor.getId(), token);
        assertNotNull(addedDocuments);
        assertTrue(addedDocuments.contains(documentToAdd));
        assertEquals(documentToAdd, addedDocuments.getFirst());
        //controllo dell' eccezione
        Doctor otherDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(otherDoctor);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.getDocumentsByOwner(otherDoctor.getId(), token);
                }
        );
        assertEquals("There is no Documents in the database for this owner", thrown.getMessage());
    }

    @Test
    public void sendDocument() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument(DocumentFixture.genTitle(), doctor.getId(), token);

        documentController.sendDocument(documentToAdd, customer.getId(), token);
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertNotNull(addedDocument);
        assertEquals(customer.getId(), addedDocument.getReceiverId());
        assertEquals(notificationDao.getNotificationsByReceiverId(customer.getId()).getFirst().getTitle(), "New document " + documentToAdd.getTitle() + " by :" + doctor.getFullName());
        Customer otherCustomer = CustomerFixture.genCustomer();
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.sendDocument(documentToAdd, otherCustomer.getId(), token);
                }
        );
        assertEquals("The person looked for in not present in the database", thrown.getMessage());
    }

    @Test
    public void attachDocumentToMedicalExam() throws Exception {
        String token = loginController.login("test@test.com", "test");
        Doctor doctor = DoctorFixture.genDoctor();
        doctorDao.insert(doctor);
        MedicalExam medicalExam = MedicalExamFixture.genMedicalExam(doctor);
        medicalExamDao.insert(medicalExam);
        Document document = DocumentFixture.genDocument(doctor);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.attachDocumentToMedicalExam(document.getId(), medicalExam.getId(), token);
                }
        );
        assertEquals("The Document looked for in not present in the database", thrown.getMessage());
        // se il documento non è nel db
        // controllare la notifica
        Document documentDb = documentController.addDocument(DocumentFixture.genTitle(), doctor.getId(), token);
        Customer customer = CustomerFixture.genCustomer();
        customerDao.insert(customer);
        documentController.sendDocument(documentDb, customer.getId(), token);
        assertEquals(notificationDao.getNotificationsByReceiverId(customer.getId()).getFirst().getTitle(), "New document " + documentDb.getTitle() + " by :" + doctor.getFullName());
        Doctor otherDoctor = DoctorFixture.genDoctor();
        doctorDao.insert(otherDoctor);
        Document documentToAdd = documentController.addDocument(DocumentFixture.genTitle(), otherDoctor.getId(), token);
        MedicalExam otherMedicalExam = MedicalExamFixture.genMedicalExam(otherDoctor);
        medicalExamDao.insert(otherMedicalExam);
        documentController.attachDocumentToMedicalExam(documentToAdd.getId(), otherMedicalExam.getId(), token);
        assertEquals(documentDao.get(documentToAdd.getId()).getMedicalExamId(), otherMedicalExam.getId());  // due medical exam a cui è stato attaccato lo stesso documento uno tramite attach e uno con add
        // documento già presente nel db

        RuntimeException otherThrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.attachDocumentToMedicalExam(documentDb.getId(), otherMedicalExam.getId(), token);
                }
        );
        assertEquals("Can't attach a document to a medicalExam which is not yours", otherThrown.getMessage());
    }

    @Test
    public void useMethodsWithoutRequiredRole() throws Exception {
        Person personAdded = personDao.getPersonByUsername("test@test.com");
        personDao.setAdmin(personAdded, false);
        String token = loginController.login("test@test.com", "test");
        SecurityException excp = assertThrowsExactly(
                SecurityException.class,
                () -> {
                    documentController.addDocument(DocumentFixture.genTitle(), personAdded.getId(), token);
                }
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> {
                    documentController.getDocumentsByReceiver(personAdded.getId(), token);
                }
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));
        excp = assertThrowsExactly(
                SecurityException.class,
                () -> {
                    documentController.getDocumentsByOwner(personAdded.getId(), token);
                }
        );
        assertTrue(excp.getMessage().matches("^Forbidden: required any of roles.*$"));

    }

    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
        connection.prepareStatement("delete from medical_exams").executeUpdate();
        connection.prepareStatement("delete from tags").executeUpdate();
        connection.prepareStatement("delete from notifications").executeUpdate();
    }
}
