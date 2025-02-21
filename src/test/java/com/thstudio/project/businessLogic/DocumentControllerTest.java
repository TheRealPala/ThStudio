package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import com.thstudio.project.domainModel.State.Available;
import com.thstudio.project.domainModel.State.Booked;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentControllerTest {
    private static DocumentController documentController;
    private static CustomerDao customerDao;
    private static DocumentDao documentDao;
    private static DoctorDao doctorDao;
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
        PersonDao personDao = new MariaDbPersonDao();
        medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        doctorDao = new MariaDbDoctorDao(personDao);
        customerDao = new MariaDbCustomerDao(personDao);
        documentDao = new MariaDbDocumentDao();
        documentController = new DocumentController(documentDao, personDao,new MariaDbNotificationDao(), medicalExamDao);

    }

    @Test
    public void addDocument() throws Exception {
        Doctor doctor = new Doctor("marco", "surname", "2000-01-01",1,"licence", 0.0);
        doctorDao.insert(doctor);
        Customer customer= new Customer("luigi", "surname", "2000-01-01",1,0.0);
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertNotNull(addedDocument);
        assertEquals(documentToAdd, addedDocument);

    }
    @Test
    public void getDocumentsByReceiver() throws Exception {
       Doctor doctor = new Doctor("marco", "surname", "2000-01-01",1,"licence", 0.0);
        doctorDao.insert(doctor);
        Customer customer= new Customer("luigi", "surname", "2000-01-01",1,0.0);
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());
        documentToAdd.setReceiverId(customer.getId());
        List<Document> addedDocuments = documentController.getDocumentsByReceiver(customer.getId());
        assertNotNull(addedDocuments);
        assertTrue(addedDocuments.contains(documentToAdd));
        assertEquals(documentToAdd, addedDocuments.get(0));
    }
    @Test
    public void getDocumentsByOwner() throws Exception {
        Doctor doctor = new Doctor("marco", "surname", "2000-01-01",1,"licence", 0.0);
        doctorDao.insert(doctor);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());  // si può usare anche document dao, ma va gestito il path
        List<Document> addedDocuments = documentController.getDocumentsByOwner(doctor.getId());
        assertNotNull(addedDocuments);
        assertTrue(addedDocuments.contains(documentToAdd));
        assertEquals(documentToAdd, addedDocuments.get(0));
    }
    @Test
    public void sendDocument() throws Exception {
        Doctor doctor = new Doctor("marco", "surname", "2000-01-01",1,"licence", 0.0);
        doctorDao.insert(doctor);
        Customer customer= new Customer("luigi", "surname", "2000-01-01",1,0.0);
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());
        documentController.sendDocument(documentToAdd, customer.getId());
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertNotNull(addedDocument);
        assertEquals(customer.getId(), addedDocument.getReceiverId());

        Document documentToAdd2 = new Document("title2","path", doctor.getId());
        documentController.sendDocument(documentToAdd2, customer.getId());
        Document addedDocument2 = documentDao.get(documentToAdd2.getId());
        assertNotNull(addedDocument2);
        assertEquals(customer.getId(), addedDocument2.getReceiverId());  // verifico che il document non immesso nel db sia stato inserito
    }
    @Test
    public void attachDocumentToMedicalExam() throws Exception {
        Doctor doctor = new Doctor("marco", "surname", "2000-01-01",1,"licence", 0.0);
        doctorDao.insert(doctor);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());
        LocalDateTime startTime = LocalDateTime.of(2026, 1, 1, 12, 40);
        LocalDateTime endTime = LocalDateTime.of(2026, 1, 1, 13, 40);
        MedicalExam medicalExam = new MedicalExam(doctor.getId(),startTime, endTime, "description", "title", 30.5);
        medicalExamDao.insert(medicalExam);
        documentController.attachDocumentToMedicalExam(documentToAdd.getId(), medicalExam.getId());
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertNotNull(addedDocument);
        assertEquals(medicalExam.getId(), addedDocument.getMedicalExamId());
    }







    @AfterEach
    void flushDb() throws SQLException {
        Connection connection = Database.getConnection();
        connection.prepareStatement("delete from people").executeUpdate();
        connection.prepareStatement("delete from documents").executeUpdate();
        connection.prepareStatement("delete from doctors").executeUpdate();
        connection.prepareStatement("delete from customers").executeUpdate();
    }




}
