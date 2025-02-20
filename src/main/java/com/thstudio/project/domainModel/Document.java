package com.thstudio.project.domainModel;

public class Document {
    private int id;
    private String title;
    private String path;
    private int ownerId;
    private int receiverId;
    private int medicalExamId;

    public Document (int id, String title, String path, int ownerId) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.ownerId = ownerId;
    }

    public Document (String title, String path, int ownerId) {
        this.title = title;
        this.path = path;
        this.ownerId = ownerId;
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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getMedicalExamId() {
        return medicalExamId;
    }

    public void setMedicalExamId(int medicalExamId) {
        this.medicalExamId = medicalExamId;
    }
}
