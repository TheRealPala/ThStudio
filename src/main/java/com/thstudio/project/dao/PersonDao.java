package com.thstudio.project.dao;

import com.thstudio.project.domainModel.Person;

public interface PersonDao extends DAO<Person, Integer> {
    public Person getPersonByUsername(String email) throws Exception;
    public boolean isDoctor(Person person) throws Exception;
    public boolean isCustomer(Person person) throws Exception;
    public boolean isAdmin(Person person) throws Exception;
    public void setAdmin(Person person, boolean value) throws Exception;
}
