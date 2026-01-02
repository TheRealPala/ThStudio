package com.thstudio.project.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Authz {
    private final JwtService jwt;

    public Authz(JwtService jwt) throws Exception {this.jwt = jwt;}

    /**
     * Valida il token e restituisce il ruolo contenuto nel claim "role".
     */
    private String role(String token) {
        jwt.validate(token);
        return jwt.getRole(token);
    }

    /**
     * Richiede che il token abbia uno qualsiasi tra i ruoli attesi.
     * Lancia SecurityException se nessun ruolo atteso coincide.
     */
    public void requireAnyRole(String token, String... expectedRoles) {
        String actual = role(token);
        Set<String> allowed = new HashSet<>(Arrays.asList(expectedRoles));
        if (actual == null || !allowed.contains(actual)) {
            throw new SecurityException("Forbidden: required any of roles " + allowed);
        }
    }
}
