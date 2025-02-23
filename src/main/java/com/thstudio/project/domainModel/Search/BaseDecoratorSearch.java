package com.thstudio.project.domainModel.Search;

import java.util.ArrayList;

public abstract class BaseDecoratorSearch implements Search {
    private final Search decoratedSearch;
    protected ArrayList<Object> arguments;

    public BaseDecoratorSearch(Search decoratedSearch) {
        this.decoratedSearch = decoratedSearch;
        arguments = new ArrayList<>();
    }

    @Override
    public String getSearchQuery() {
        return decoratedSearch.getSearchQuery();
    }

    @Override
    public ArrayList<Object> getArguments() {
        return arguments;
    }

}
