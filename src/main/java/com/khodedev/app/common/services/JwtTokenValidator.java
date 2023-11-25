package com.khodedev.app.common.services;

import io.jsonwebtoken.Jwts;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Log
public class JwtTokenValidator {

    @Value("${app.jwt.public-key}")
    private String publicKey;

    public boolean validateToken(String token) {

        try {
            PublicKey key = parsePublicKey(publicKey);
            // Verify and parse the token
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.info("Invalid token: " + e.getMessage());
            return false;
        }
    }

    private PublicKey parsePublicKey(String publicKey) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKey);
        var keySpec = new X509EncodedKeySpec(publicBytes);
        var keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
