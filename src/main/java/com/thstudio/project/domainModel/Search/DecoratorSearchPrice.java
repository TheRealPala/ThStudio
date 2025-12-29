package com.thstudio.project.domainModel.Search;

public class DecoratorSearchPrice extends BaseDecoratorSearch {

    public DecoratorSearchPrice(Search decoratedSearch, double maxPrice) {
        super(decoratedSearch);
        this.arguments = decoratedSearch.getArguments();
        this.arguments.add(maxPrice);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND price <= ?";
    }
}

