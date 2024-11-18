package domainModel;

public class Tag {
    protected String tags;
    protected String tagsType;

    public Tag(){};
    public Tag(String tags, String tags_type ){
        this.tags=tags;
        this.tagsType=tags_type;
    };

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags_type() {
        return tagsType;
    }

    public void setTags_type(String tags_type) {
        this.tagsType = tags_type;
    }



}
