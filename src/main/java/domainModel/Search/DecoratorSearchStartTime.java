package domainModel.Search;

import java.time.LocalDateTime;

public class DecoratorSearchStartTime extends BaseDecoratorSearch{
    private final LocalDateTime startTime;

    public DecoratorSearchStartTime(Search decoratedSearch, LocalDateTime minStartTime){
        super(decoratedSearch);
        this.startTime = minStartTime;
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND startTime >= '" + startTime.toString() + "'";
    }
}
