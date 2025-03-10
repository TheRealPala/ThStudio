package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Person;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class PersonController {
    private final PersonDao personDao;

    public PersonController(PersonDao personDao) {
        this.personDao = personDao;
    }

    /**
     * Add a new Person in the DB
     *
     * @param newPerson The new person
     */
    public Person addPerson(Person newPerson) throws Exception {
        personDao.insert(newPerson);
        return newPerson;
    }

    /**
     * Update the person with the corresponding CF
     */
    public String updatePerson(Person person) throws Exception {
        personDao.update(person);
        return "Person updated successfully";
    }

    /**
     * Remove from the DB the person with the corresponding CF
     */
    public boolean deletePerson(int id) throws Exception {
        return this.personDao.delete(id);
    }

    /**
     * Returns a read-only list of person
     *
     * @return The list of person
     */
    public List<Person> getAllPersons() throws Exception {
        return unmodifiableList(personDao.getAll());
    }

    /**
     * Returns the person with the corresponding CF
     */
    public Person getPerson(int id) throws Exception {
        return personDao.get(id);
    }
}
