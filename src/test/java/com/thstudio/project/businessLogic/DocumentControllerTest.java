package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.*;
import com.thstudio.project.domainModel.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class DocumentControllerTest {
    private static DocumentController documentController;
    private static DocumentDao documentDao;
    private static PersonDao personDao;
    private static DoctorDao doctorDao;
    private static CustomerDao customerDao;
    private static MedicalExamDao medicalExamDao;
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
        documentDao = new MariaDbDocumentDao();
        personDao = new MariaDbPersonDao();
        doctorDao = new MariaDbDoctorDao(personDao);
        customerDao = new MariaDbCustomerDao(personDao);
        notificationDao = new MariaDbNotificationDao();
        medicalExamDao = new MariaDbMedicalExamDao(new MariaDbTagDao());
        documentController = new DocumentController(documentDao, personDao, notificationDao, medicalExamDao);

    }

    @Test
    void addDocument() throws Exception {
        Person personToAdd = new Person("Marco", "Rossi", "2000-10-01", 1000);
        personDao.insert(personToAdd);
        assertNotEquals(personToAdd.getId(), 0);
        Document documentToAdd = documentController.addDocument("Documento1", personToAdd.getId());
        Document addedDocument  = documentDao.get(documentToAdd.getId());
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
        documentDao.update(documentToAdd);
        List<Document> addedDocuments = documentController.getDocumentsByReceiver(customer.getId());
        assertNotNull(addedDocuments);
        assertTrue(addedDocuments.contains(documentToAdd));
        assertEquals(documentToAdd, addedDocuments.get(0));
        //controllo dell' eccezione
        Customer customer2= new Customer("luigi", "surname", "2000-01-01",2,0.0);
        customerDao.insert(customer2);

         RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.getDocumentsByReceiver(customer2.getId());
                }
            );
         assertEquals(thrown.getMessage(), "There is no Documents in the database for this receiver" );

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
        //controllo dell' eccezione
        Doctor doctor2 = new Doctor("marco", "surname", "2000-01-01",2,"licence", 0.0);
        doctorDao.insert(doctor2);
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.getDocumentsByOwner(doctor2.getId());
                }
        );
        assertEquals(thrown.getMessage(), "There is no Documents in the database for this owner" );
    }
    @Test
    public void sendDocument() throws Exception {
        Doctor doctor = new Doctor("marco", "surname", "2000-01-01","licence", 0.0);
        doctorDao.insert(doctor);
        Customer customer= new Customer("luigi", "surname", "2000-01-01",1,0.0);
        customerDao.insert(customer);
        Document documentToAdd = documentController.addDocument("title", doctor.getId());

        documentController.sendDocument(documentToAdd, customer.getId());
        Document addedDocument = documentDao.get(documentToAdd.getId());
        assertNotNull(addedDocument);
        assertEquals(customer.getId(), addedDocument.getReceiverId());
        assertEquals(notificationDao.getNotificationsByReceiverId(customer.getId()).get(0).getTitle(), "New document " + documentToAdd.getTitle() + " by :" + doctor.getFullName());
        Customer customer1= new Customer("luigi", "surname", "2000-01-01",2,0.0);
        RuntimeException thrown1 = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.sendDocument(documentToAdd, customer1.getId());
                }
        );
        assertEquals(thrown1.getMessage(), "The person looked for in not present in the database");
        // verifico che il customer sia nel db


    }
    @Test
    public void attachDocumentToMedicalExam() throws Exception {

        Doctor doctor1 = new Doctor("marco", "surname", "2000-01-01","licence", 0.0);
        doctorDao.insert(doctor1);
        MedicalExam medicalExam1 = new MedicalExam(doctor1.getId(),"2026-01-01 12:40:00","2026-01-01 13:40:00", "description", "title", 30.5);
        medicalExamDao.insert(medicalExam1);
        Document document1 = new Document("title","path", doctor1.getId());
        RuntimeException thrown = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.attachDocumentToMedicalExam(document1.getId(), medicalExam1.getId());
                }
        );
        assertEquals(thrown.getMessage(), "The Document looked for in not present in the database");
        // se il documento non è nel db



        // controllare la notifica
        Document documentDb = documentController.addDocument("title", doctor1.getId());
        Doctor doctor2 = new Doctor("marco", "surname", "2000-01-01",2,"licence", 0.0);
        doctorDao.insert(doctor2);
        Document documentToAdd = documentController.addDocument("title", doctor2.getId());
        MedicalExam medicalExam = new MedicalExam(doctor2.getId(),"2026-01-01 12:40:00","2026-01-01 13:40:00", "description", "title", 30.5);
        medicalExamDao.insert(medicalExam);
        documentController.attachDocumentToMedicalExam(documentToAdd.getId(), medicalExam.getId());
        assertEquals(documentDao.get(documentToAdd.getId()).getMedicalExamId(),medicalExam.getId());  // due medical exam a cui è stato attaccato lo stesso documento uno tramite attach e uno con add
        // documento già presente nel db

        RuntimeException thrown1 = assertThrowsExactly(RuntimeException.class,
                () -> {
                    documentController.attachDocumentToMedicalExam(documentDb.getId(), medicalExam.getId());
                }
        );
        assertEquals(thrown1.getMessage(), "Can't attach a document to a medicalExam which is not yours");



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
