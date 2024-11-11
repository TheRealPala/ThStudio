public class Tags {
    public Tags(){};
    public Tags(String tags,String tags_type ){
        this.tags=tags;
        this.tags_type=tags_type;
    };

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags_type() {
        return tags_type;
    }

    public void setTags_type(String tags_type) {
        this.tags_type = tags_type;
    }

    protected String tags;
    protected String tags_type;


}
