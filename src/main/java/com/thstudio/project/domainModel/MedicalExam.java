package com.thstudio.project.domainModel;

import com.thstudio.project.domainModel.State.State;
import com.thstudio.project.domainModel.Tags.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MedicalExam {
    private String title;
    private String description;
    private State state;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final int idDoctor;
    private int idCustomer;
    private int id;
    private double price;
    private ArrayList<Document> documents;
    private ArrayList<Tag> tags;

    public MedicalExam(int idDoctor, String startTime, String endTime,
                       String description, String title, double price) {
        this.tags = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.idDoctor = idDoctor;
        this.startTime = this.parseDate(startTime);
        this.endTime = this.parseDate(endTime);
        this.description = description;
        this.title = title;
        this.price = price;
    }

    public MedicalExam(int id, int idDoctor, String startTime, String endTime,
                       String description, String title, double price) {
        this.id = id;
        this.idDoctor = idDoctor;
        this.startTime = this.parseDate(startTime);
        this.endTime = this.parseDate(endTime);
        this.description = description;
        this.title = title;
        this.price = price;
        this.tags = new ArrayList<>();
        this.documents = new ArrayList<>();
    }
    public MedicalExam(int id, int idDoctor, int idCustomer, String startTime, String endTime,
                       String description, String title, double price, State state) {
        this.id = id;
        this.idDoctor = idDoctor;
        this.startTime = this.parseDate(startTime);
        this.endTime = this.parseDate(endTime);
        this.description = description;
        this.title = title;
        this.price = price;
        this.tags = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.idCustomer=idCustomer;
        this.state = state;
    }

    private LocalDateTime parseDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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

    public void addTag(Tag tagToAdd) {
        this.tags.add(tagToAdd);
    }

    public void removeTag(Tag tagToRemove) {
        this.tags.remove(tagToRemove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicalExam that = (MedicalExam) o;
        return getIdDoctor() == that.getIdDoctor() && getIdCustomer() == that.getIdCustomer() && getId() == that.getId() && Double.compare(getPrice(), that.getPrice()) == 0 && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getState(), that.getState()) && Objects.equals(getStartTime(), that.getStartTime()) && Objects.equals(getEndTime(), that.getEndTime()) && Objects.equals(getDocuments(), that.getDocuments()) && Objects.equals(getTags(), that.getTags());
    }

    public void setStartTimeFromString(String startTime) {
        this.startTime = this.parseDate(startTime);
    }

    public void setEndTimeFromString(String endTime) {
        this.endTime = this.parseDate(endTime);
    }
}
