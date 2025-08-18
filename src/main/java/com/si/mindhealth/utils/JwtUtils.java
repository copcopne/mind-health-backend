package com.si.mindhealth.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {
    // Nên để trong cấu hình môi trường (ENV/Secrets)
    private static final String SECRET_KEY = "CHANGE_ME_TO_A_32+_BYTE_SECRET_1234567890abcd";
    private static final long ACCESS_TOKEN_EXP_MS  = 15 * 60 * 1000;       // 15 phút
    private static final long REFRESH_TOKEN_EXP_MS = 7L * 24 * 60 * 60 * 1000; // 7 ngày

    private static final JWSAlgorithm ALG = JWSAlgorithm.HS256;

    private static SignedJWT sign(JWTClaimsSet claims) throws Exception {
        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        SignedJWT jwt = new SignedJWT(new JWSHeader(ALG), claims);
        jwt.sign(signer);
        return jwt;
    }

    public static String generateAccessToken(String username, String role) throws Exception {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ACCESS_TOKEN_EXP_MS);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(username)
                .claim("role", role)
                .claim("typ", "access")
                .issueTime(now)
                .expirationTime(exp)
                .build();

        return sign(claims).serialize();
    }

    public static String generateRefreshToken(String username) throws Exception {
        Date now = new Date();
        Date exp = new Date(now.getTime() + REFRESH_TOKEN_EXP_MS);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(username)
                .claim("typ", "refresh")
                .issueTime(now)
                .expirationTime(exp)
                .build();

        return sign(claims).serialize();
    }

    public static boolean isValid(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            if (!jwt.verify(verifier)) return false;
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public static String getSubject(String token) throws Exception {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public static String getRole(String token) throws Exception {
        return SignedJWT.parse(token).getJWTClaimsSet().getStringClaim("role");
    }

    public static String getType(String token) throws Exception {
        return SignedJWT.parse(token).getJWTClaimsSet().getStringClaim("typ"); // "access" | "refresh"
    }
}
