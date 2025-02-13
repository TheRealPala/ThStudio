package domainModel;

public class Document {
    private int id;
    private String title;
    private String path;
    private Person owner;
    private Person receiver;
    private MedicalExam medicalExam;

    public Document (int id, String title, String path, Person owner) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.owner = owner;
    }

    public Document (String title, String path, Person owner) {
        this.title = title;
        this.path = path;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Person getReceiver() {
        return receiver;
    }

    public void setReceiver(Person receiver) {
        this.receiver = receiver;
    }

    public MedicalExam getMedicalExam() {
        return medicalExam;
    }

    public void setMedicalExam(MedicalExam medicalExam) {
        this.medicalExam = medicalExam;
    }
}
