package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.security.Authn;


public class LoginController {
    private final PersonDao personDao;
    private final Authn authn;

    public LoginController(PersonDao personDao, Authn authn) {
        this.personDao = personDao;
        this.authn = authn;
    }



    private String getRole(Person person) throws Exception {
        String role = "person";
        if (this.personDao.isAdmin(person)) {
            return "admin";
        } else if (this.personDao.isCustomer(person)) {
            return "customer";
        } else if (this.personDao.isDoctor(person)) {
            return "doctor";
        }
        return role;
    }

    public String login(String email, String password) throws Exception {
        Person person = this.personDao.getPersonByUsername(email);
        if (this.authn.checkPassword(password, person.getPassword())) {
            String role = getRole(person);
            return this.authn.createToken(person.getId(), role);
        } else {
            throw new SecurityException("Invalid password");
        }
    }


}
