package com.thstudio.project.dao;

import com.thstudio.project.domainModel.Person;

public interface PersonDao extends DAO<Person, Integer> {
    public Person getPersonByUsername(String email) throws Exception;
}
