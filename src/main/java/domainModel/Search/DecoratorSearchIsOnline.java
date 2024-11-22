package domainModel.Search;

public class DecoratorSearchIsOnline extends BaseDecoratorSearch {
    private final String online;

    public DecoratorSearchIsOnline(Search decoratedSearch, String online){
        super(decoratedSearch);
        this.online = online;
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND L.idLesson IN (SELECT idLesson FROM lessonsTags WHERE tagType = 'Online' AND tag = '" + online  + "' )";
    }
}
