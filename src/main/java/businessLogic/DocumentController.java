package businessLogic;

import dao.DocumentDao;
import dao.MedicalExamDao;
import dao.NotificationDao;
import dao.PersonDao;
import domainModel.Document;
import domainModel.MedicalExam;
import domainModel.Notification;
import domainModel.Person;
import domainModel.State.Booked;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.SQLException;
import java.util.List;

public class DocumentController {
    private final DocumentDao documentDao;
    private final String baseDocumentPath;
    private final PersonDao personDao;
    private final NotificationDao notificationDao;
    private final MedicalExamDao medicalExamDao;

    public DocumentController(DocumentDao documentDao, PersonDao personDao, NotificationDao notificationDao,
                              MedicalExamDao medicalExamDao) {
        this.documentDao = documentDao;
        this.personDao = personDao;
        this.baseDocumentPath = Dotenv.configure().directory("config").load().get("BASE_DOC_DIR_PATH");
        this.notificationDao = notificationDao;
        this.medicalExamDao = medicalExamDao;
    }

    private void addDocument(String title, int ownerId) throws Exception {
        Person person = personDao.get(ownerId);
        Document document = new Document(title, this.baseDocumentPath + title, person.getId());
        this.documentDao.insert(document);
    }

    public List<Document> getDocumentsByReceiver(int receiverId) throws Exception {
        return this.documentDao.getByReceiver(receiverId);
    }

    public List<Document> getDocumentsByOwner(int ownerId) throws Exception {
        return this.documentDao.getByOwner(ownerId);
    }

    public void sendDocument(Document document, int receiverId) throws Exception {
        boolean isAlreadyPersisted = true;
        try {
            this.documentDao.get(document.getId());
        } catch (Exception e) {
            System.err.println("The document you want to send is not present in the db");
           isAlreadyPersisted = false;
        }
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

    public void attachDocumentToMedicalExam(int documentId, int medicalExamId) throws Exception {
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
        this.documentDao.update(document);
    }
}
