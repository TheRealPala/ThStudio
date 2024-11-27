package BusinessLogic;

import dao.DAO;
import domainModel.Person;

import java.util.List;
import static java.util.Collections.unmodifiableList;

public class PersonController <T extends Person>{
    private DAO<T, Integer> dao;

    public PersonController(DAO<T, Integer> dao){
        this.dao = dao;
    }

    /**
     * Add a new Person in the DB
     *
     * @param newPerson The new person
     */
    public String addPerson(T newPerson) throws Exception {
        dao.insert(newPerson);
        return "Person added successfully";
    }

    /**
     * Update the person with the corresponding CF
     */
    public String updatePerson(T person) throws Exception {
        dao.update(person);
        return "Person updated successfully";
    }

    /**
     * Remove from the DB the person with the corresponding CF
     */
    public boolean deletePerson(int id) throws Exception {
        return this.dao.delete(id);
    }

    /**
     * Returns a read-only list of person
     *
     * @return The list of person
     */
    public List<T> getAllPersons() throws Exception {
        return unmodifiableList(dao.getAll());
    }

        /** Returns the person with the corresponding CF */
    public T getPerson(int id) throws Exception {
        return dao.get(id);
    }
}
