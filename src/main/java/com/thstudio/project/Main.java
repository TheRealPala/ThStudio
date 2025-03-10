package com.thstudio.project;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import com.thstudio.project.dao.*;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.time.Instant;
import java.util.Base64;

public class Main {
    public static RSAPrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(privateKeyPath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
    public static RSAPublicKey loadPublicKey(String publicKeyPath) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("user.dir"));
        Dotenv dotenv = Dotenv.configure().directory("config").load();
        Database.setDbHost(dotenv.get("DB_HOST"));
        Database.setDbName(dotenv.get("DB_NAME_DEFAULT"));
        Database.setDbTestName(dotenv.get("DB_NAME_TEST"));
        Database.setDbUser(dotenv.get("DB_USER"));
        Database.setDbPassword(dotenv.get("DB_PASSWORD"));
        Database.setDbPort(dotenv.get("DB_PORT"));
        System.out.println(Database.testConnection(false, false));

        RSAPrivateKey privateKey = loadPrivateKey("config/security/sign_pkcs8.pem");
        RSAPublicKey publicKey = loadPublicKey("config/security/sign_pub.pem");
        System.out.println("Chiave privata caricata: " + privateKey);
        System.out.println("Chiave pubblica caricata: " + publicKey);
        String token = "";
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("id", 10203)
                    .withExpiresAt(Instant.now())
                    .sign(algorithm);
            System.out.println(token);
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
        }

        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    // specify any specific claim validations
                    .withIssuer("auth0")
                    .acceptExpiresAt(3600)
                    // reusable verifier instance
                    .build();

            decodedJWT = verifier.verify(token);
            System.out.println(decodedJWT.getClaim("id"));
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
        }
        //BCrypt
        String stringToHash = "prova123";
        String hashedPassword = BCrypt.hashpw(stringToHash, BCrypt.gensalt(12));
        System.out.println(hashedPassword);
        System.out.println(BCrypt.checkpw("puppa", hashedPassword));
        System.out.println(BCrypt.checkpw(stringToHash, hashedPassword));
    }
}
