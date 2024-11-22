package domainModel;

public class Tag {
    protected String tag;
    protected String tagType;

    public Tag(String tag, String tagType ){
        this.tag = tag;
        this.tagType = tagType;
    };

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
}
