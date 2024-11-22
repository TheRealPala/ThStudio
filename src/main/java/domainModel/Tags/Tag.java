package domainModel.Tags;

public abstract class Tag {
    protected String tag;

    protected String typeOfTag;

    public String getTypeOfTag() {
        return this.typeOfTag;
    }

    public String getTag(){
        return this.tag;
    }
}
