package com.thstudio.project.security;


import org.mindrot.jbcrypt.BCrypt;

public class Authn {
    private final JwtService jwt;

    public Authn(JwtService jwt) {
        this.jwt = jwt;
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public String createToken(int id, String role) {
        return this.jwt.createToken(id, role);
    }
}
