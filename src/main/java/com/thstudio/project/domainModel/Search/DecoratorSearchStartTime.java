package com.thstudio.project.domainModel.Search;

import java.time.LocalDateTime;

public class DecoratorSearchStartTime extends BaseDecoratorSearch{

    public DecoratorSearchStartTime(Search decoratedSearch, LocalDateTime minStartTime){
        super(decoratedSearch);
        this.arguments = decoratedSearch.getArguments();
        this.arguments.add(minStartTime.toString());
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND start_time >= ?";
    }
}
