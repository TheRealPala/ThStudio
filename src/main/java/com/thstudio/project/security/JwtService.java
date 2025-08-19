package com.thstudio.project.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final Duration defaultTtl;

    public JwtService() throws Exception {
        // Configurazione hardcoded
        String hardcodedIssuer = "auth0";
        Duration hardcodedTtl = Duration.ofHours(1);
        long hardcodedLeewaySeconds = 60L;

        RSAPrivateKey privateKey = loadPrivateKey(Path.of("config/security/keys/sign_private.pem"));
        RSAPublicKey publicKey = loadPublicKey(Path.of("config/security/keys/sign_public.pem"));

        this.algorithm = Algorithm.RSA256(publicKey, privateKey);
        this.issuer = hardcodedIssuer;
        this.defaultTtl = hardcodedTtl;
        this.verifier = JWT
                .require(Algorithm.RSA256(publicKey, null))
                .withIssuer(this.issuer)
                .acceptLeeway(hardcodedLeewaySeconds)
                .build();
    }

    public String createToken(int id, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(this.defaultTtl);
        return JWT.create()
                .withIssuer(this.issuer)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("id", id)
                .withClaim("role", role)
                .sign(this.algorithm);
    }

    public void validate(String token) {
        verifier.verify(token);
    }

    public String getRole(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("role").asString();
    }

    public DecodedJWT decode(String token) {
        return verifier.verify(token);
    }

    private static RSAPrivateKey loadPrivateKey(Path path) throws Exception {
        String pem = Files.readString(path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private static RSAPublicKey loadPublicKey(Path path) throws Exception {
        String pem = Files.readString(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
