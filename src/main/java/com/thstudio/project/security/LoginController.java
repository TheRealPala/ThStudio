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

    public String login(String email, String password) throws Exception {
        String token = "";
        Person person = this.personDao.getPersonByUsername(email);
        if (checkPassword(password, person.getPassword())) {
            token = createToken(person.getId());
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
