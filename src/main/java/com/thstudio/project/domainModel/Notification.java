package com.thstudio.project.domainModel;

import java.util.Objects;

public class Notification {
    private int id;
    private String title;
    private int receiverId;

    public Notification(int id, String title, int receiverId) {
        this.id = id;
        this.title = title;
        this.receiverId = receiverId;
    }

    public Notification(String title, int receiverId) {
        this.title = title;
        this.receiverId = receiverId;
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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;
        return getId() == that.getId() && getReceiverId() == that.getReceiverId() && Objects.equals(getTitle(), that.getTitle());
    }
}
