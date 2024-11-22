package domainModel.Search;

public class DecoratorSearchIsOnline extends BaseDecoratorSearch {
    private final boolean online;

    public DecoratorSearchIsOnline(Search decoratedSearch, boolean online){
        super(decoratedSearch);
        this.online = online;
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND L.idLesson IN (SELECT idLesson FROM lessonsTags WHERE tagType = 'Online' AND tag = '" + online  + "' )";
    }
}
