package com.thstudio.project.businessLogic;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thstudio.project.dao.PersonDao;
import com.thstudio.project.domainModel.Person;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

public class AuthorizedController {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private PersonDao personDao;

    public AuthorizedController(PersonDao personDao) throws Exception {
        this.privateKey = loadPrivateKey();
        this.publicKey = loadPublicKey();
        this.personDao = personDao;
    }

    private RSAPrivateKey loadPrivateKey() throws Exception {
        String key = new String(Files.readAllBytes(Paths.get("config/security/sign_pkcs8.pem")))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    private RSAPublicKey loadPublicKey() throws Exception {
        String key = new String(Files.readAllBytes(Paths.get("config/security/sign_pub.pem")))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    protected String createToken(int id) {
        String token = "";
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("id", id)
                    .withExpiresAt(Instant.now())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
        }
        return token;
    }

    protected boolean validateToken(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT;
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        JWTVerifier verifier = JWT.require(algorithm)
                // specify any specific claim validations
                .withIssuer("auth0")
                .acceptExpiresAt(3600)
                // reusable verifier instance
                .build();

        verifier.verify(token);
        return true;
    }

    protected String hashPassword(String stringToHash) {
        return BCrypt.hashpw(stringToHash, BCrypt.gensalt(12));
    }

    protected String login(String email, String password) throws Exception {
        String token = "";
        Person person = this.personDao.getPersonByUsername(email);
        if (BCrypt.checkpw(password, person.getPassword())) {
            token = createToken(person.getId());
        }
        return token;
    }
}
