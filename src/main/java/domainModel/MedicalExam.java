package domainModel;

import domainModel.State.State;
import domainModel.Tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MedicalExam {
    private String title;
    private String description;
    private State state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final int idDoctor;
    private String stateExtraInfo;
    private int idCustomer;
    private int id;
    private double price;
    private ArrayList<Document> documents;
    private ArrayList<Tag> tags;

    public MedicalExam(int idDoctor, LocalDateTime startTime, LocalDateTime endTime,
                       String description, String title, double price) {
        this.tags = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.idDoctor = idDoctor;
        this.endTime = endTime;
        this.startTime = startTime;
        this.description = description;
        this.title = title;
        this.price = price;
    }

    public MedicalExam(int id, int idDoctor, LocalDateTime startTime, LocalDateTime endTime,
                       String description, String title, double price) {
        this.tags = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.idDoctor = idDoctor;
        this.endTime = endTime;
        this.startTime = startTime;
        this.description = description;
        this.title = title;
        this.price = price;
    }
    public MedicalExam(int idDoctor, LocalDateTime endTime, LocalDateTime startTime,
                       String description, String title, double price, ArrayList<Tag> tags) {
        this.tags = tags;
        this.documents = new ArrayList<>();
        this.idDoctor = idDoctor;
        this.endTime = endTime;
        this.startTime = startTime;
        this.description = description;
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getStateExtraInfo() {
        return stateExtraInfo;
    }

    public void setStateExtraInfo(String stateExtraInfo) {
        this.stateExtraInfo = stateExtraInfo;
    }

    public int getIdDoctor() {
        return idDoctor;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
