package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.DocumentDao;
import com.thstudio.project.dao.MedicalExamDao;
import com.thstudio.project.dao.NotificationDao;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Document;
import com.thstudio.project.domainModel.MedicalExam;
import com.thstudio.project.domainModel.Notification;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.domainModel.State.Booked;
import com.thstudio.project.security.Authz;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

public class DocumentController {
    private final DocumentDao documentDao;
    private final String baseDocumentPath;
    private final PersonDao personDao;
    private final NotificationDao notificationDao;
    private final MedicalExamDao medicalExamDao;
    private final com.thstudio.project.security.Authz authz;

    public DocumentController(DocumentDao documentDao, PersonDao personDao, NotificationDao notificationDao,
                              MedicalExamDao medicalExamDao) throws Exception {
        this.documentDao = documentDao;
        this.personDao = personDao;
        this.baseDocumentPath = Dotenv.configure().directory("config").load().get("BASE_DOC_DIR_PATH");
        this.notificationDao = notificationDao;
        this.medicalExamDao = medicalExamDao;
        this.authz = new Authz();
    }

    public Document addDocument(String title, int ownerId, String token) throws Exception {

        this.authz.requireAnyRole(token, "doctor", "customer", "admin");
        Person person = personDao.get(ownerId);
        Document document = new Document(title, this.baseDocumentPath + title, person.getId());
        this.documentDao.insert(document);
        return document;
    }

    public List<Document> getDocumentsByReceiver(int receiverId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "customer", "admin");
        return this.documentDao.getByReceiver(receiverId);
    }

    public List<Document> getDocumentsByOwner(int ownerId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "customer", "admin");
        return this.documentDao.getByOwner(ownerId);
    }

    public void sendDocument(Document document, int receiverId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "customer", "admin");
        boolean isAlreadyPersisted = true;
        try {
            this.documentDao.get(document.getId());
        } catch (Exception e) {
            System.err.println("The document you want to send is not present in the db");
            isAlreadyPersisted = false;
        }
        this.personDao.get(receiverId);
        document.setReceiverId(receiverId);
        Person documentOwner = this.personDao.get(document.getOwnerId());
        if (isAlreadyPersisted) {
            this.documentDao.update(document);
        } else {
            this.documentDao.insert(document);
        }
        Notification nd = new Notification("New document " + document.getTitle() + " by :" + documentOwner.getFullName(), receiverId);
        this.notificationDao.insert(nd);
    }

    public void attachDocumentToMedicalExam(int documentId, int medicalExamId, String token) throws Exception {
        this.authz.requireAnyRole(token, "doctor", "admin");

        MedicalExam medicalExam = this.medicalExamDao.get(medicalExamId);
        Document document = this.documentDao.get(documentId);
        if (medicalExam.getIdDoctor() != document.getOwnerId()) {
            throw new RuntimeException("Can't attach a document to a medicalExam which is not yours");
        }
        Person owner = this.personDao.get(document.getOwnerId());
        int receiverId = medicalExam.getIdCustomer();
        if (medicalExam.getState() instanceof Booked) {
            document.setReceiverId(receiverId);
            Notification nd = new Notification("New document (" + document.getTitle() + ") by: " + owner.getFullName(), receiverId);
            notificationDao.insert(nd);
        }
        document.setMedicalExamId(medicalExam.getId());
        documentDao.update(document);
    }
}
