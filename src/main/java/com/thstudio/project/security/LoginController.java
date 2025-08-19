package com.thstudio.project.security;

import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Person;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController extends AuthorizedController {
    private PersonDao personDao;


    public LoginController(PersonDao personDao) throws Exception {
        this.personDao = personDao;
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
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
        String token = "";
        Person person = this.personDao.getPersonByUsername(email);
        if (checkPassword(password, person.getPassword())) {
            String role = getRole(person);
            token = createToken(person.getId(), role);
        }
        else {
            throw new SecurityException("Invalid password");
        }
        return token;
    }

    public String hashPassword (String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
