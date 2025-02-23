package com.thstudio.project.domainModel.Search;

public class DecoratorSearchIsOnline extends BaseDecoratorSearch {

    public DecoratorSearchIsOnline(Search decoratedSearch, String online) {
        super(decoratedSearch);
        this.arguments = super.getArguments();
        this.arguments.add(online);
    }

    @Override
    public String getSearchQuery() {
        return super.getSearchQuery() + " AND m.id IN (SELECT id_medical_exam FROM medical_exams_tags WHERE tag_type = 'Online' AND tag = ?)";
    }
}
