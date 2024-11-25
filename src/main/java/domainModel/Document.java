package domainModel;

public class Document {
    private String title;
    private String path;
    private int id;

    public Document (int id, String title, String path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public Document (String title, String path) {
        this.title = title;
        this.path = path;
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
}
