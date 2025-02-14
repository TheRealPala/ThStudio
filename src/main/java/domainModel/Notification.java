package domainModel;

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
}
