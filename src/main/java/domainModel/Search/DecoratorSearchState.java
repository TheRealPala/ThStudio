package domainModel.Search;

public class DecoratorSearchState extends BaseDecoratorSearch {
    public DecoratorSearchState(Search decoratedSearch, String state) {
        super(decoratedSearch);
        this.arguments = super.getArguments();
        this.arguments.add(state);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND m.state = ?";
    }
}
