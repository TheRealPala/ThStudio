package com.thstudio.project.domainModel.Search;

public class DecoratorSearchZone extends BaseDecoratorSearch {

    public DecoratorSearchZone(Search decoratedSearch, String zone) {
        super(decoratedSearch);
        this.arguments = super.getArguments();
        this.arguments.add(zone);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND m.id IN (SELECT id_medical_exam FROM medical_exams_tags WHERE" +
                " tag_type = 'Zone' AND tag = ? )";
    }

}
