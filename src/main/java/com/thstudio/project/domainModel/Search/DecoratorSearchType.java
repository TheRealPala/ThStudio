package com.thstudio.project.domainModel.Search;

public class DecoratorSearchType extends BaseDecoratorSearch {

    public DecoratorSearchType(Search decoratedSearch, String type) {
        super(decoratedSearch);
        this.arguments = decoratedSearch.getArguments();
        this.arguments.add(type);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND m.id IN (SELECT id_medical_exam FROM medical_exams_tags WHERE" +
                " tag_type = 'Type' AND tag = ? )";
    }

}
