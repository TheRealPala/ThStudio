package domainModel.Search;

public class DecoratorSearchIsOnline extends BaseDecoratorSearch {

    public DecoratorSearchIsOnline(Search decoratedSearch, String online){
        super(decoratedSearch);
        this.arguments = super.getArguments();
        this.arguments.add(online);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND L.idLesson IN (SELECT idLesson FROM lessonsTags WHERE tagType = 'Online' AND tag = ? )";
    }
}
