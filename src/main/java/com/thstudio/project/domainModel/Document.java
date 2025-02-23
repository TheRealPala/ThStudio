package com.thstudio.project.domainModel;

import java.util.Objects;

public class Document {
    private int id;
    private String title;
    private String path;
    private int ownerId;
    private int receiverId;
    private int medicalExamId;

    public Document(int id, String title, String path, int ownerId) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.ownerId = ownerId;
    }

    public Document(String title, String path, int ownerId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Document document = (Document) o;
        return getId() == document.getId() && getOwnerId() == document.getOwnerId() && getReceiverId() == document.getReceiverId() && getMedicalExamId() == document.getMedicalExamId() && Objects.equals(getTitle(), document.getTitle()) && Objects.equals(getPath(), document.getPath());
    }
}
