package domainModel.Search;


import java.util.ArrayList;
import java.util.List;

public class SearchConcrete implements Search {

    private final String query;

    public SearchConcrete() {
        this.query = "select * from medical_exams m WHERE 1 = 1";
    }

    @Override
    public String getSearchQuery() {
        return query;
    }

    @Override
    public ArrayList<Object> getArguments() {
        return new ArrayList<>();
    }

}
