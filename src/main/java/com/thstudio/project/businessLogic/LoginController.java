package com.thstudio.project.businessLogic;

import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Person;
import com.thstudio.project.security.JwtService;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    private final PersonDao personDao;
    private final JwtService jwtService;

    public LoginController(PersonDao personDao) throws Exception {
        this.personDao = personDao;
        this.jwtService = new JwtService();
    }

    private boolean checkPassword(String password, String hashedPassword) {
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
        Person person = this.personDao.getPersonByUsername(email);
        if (checkPassword(password, person.getPassword())) {
            String role = getRole(person);
            return jwtService.createToken(person.getId(), role);
        } else {
            throw new SecurityException("Invalid password");
        }
    }


}
