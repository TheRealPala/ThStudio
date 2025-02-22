package com.thstudio.project.domainModel.Search;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DecoratorSearchStartTime extends BaseDecoratorSearch{

    public DecoratorSearchStartTime(Search decoratedSearch, String minStartDateTimeString){
        super(decoratedSearch);
        this.arguments = decoratedSearch.getArguments();
        LocalDateTime minStartDateTime = LocalDateTime.parse(minStartDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.arguments.add(minStartDateTime.toString());
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND start_time >= ?";
    }
}
