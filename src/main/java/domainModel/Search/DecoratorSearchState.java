package domainModel.Search;

public class DecoratorSearchState extends BaseDecoratorSearch {
    private final String state;
    public DecoratorSearchState(Search decoratedSearch, String state) {
        super(decoratedSearch);
        this.state = state;
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND m.state = '" + state + "'";
    }
}
