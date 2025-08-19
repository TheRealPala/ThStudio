package com.thstudio.project.security;


import org.mindrot.jbcrypt.BCrypt;

public class Authn {

    public Authn() {
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
