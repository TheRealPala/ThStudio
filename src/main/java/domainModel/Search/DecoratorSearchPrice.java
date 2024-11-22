package domainModel.Search;

public class DecoratorSearchPrice extends BaseDecoratorSearch {
    private final double maxPrice;

    public DecoratorSearchPrice(Search decoratedSearch, double maxPrice){
        super(decoratedSearch);
        this.maxPrice = maxPrice;
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND price <= '" + maxPrice + "'";
    }
}

