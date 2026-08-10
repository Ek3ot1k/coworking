package com.example.coworking.security;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JWTUtilTest {

    private JWTUtil jwtUtil;

    @BeforeEach
    void setUp(){
        jwtUtil=new JWTUtil();

        ReflectionTestUtils.setField(jwtUtil,"secret", "super_secret_key_1234567890_for_testing");
    }

    @Test
    void generateToken_And_ValidateToken_ShouldWorkCorrectly(){
        String email = "test@test.com";
        String token=jwtUtil.generateToken(email);

        assertNotNull(token,"Токен не должен быть null");
        assertFalse(token.isEmpty(),"Токен не должен быть пустым");

        String[] parts=token.split("\\.");
        assertEquals(3,parts.length,"Токен должен состоять из трех частей (header.payload.signature)");

        String extractedEmail=jwtUtil.validateTokenAndRetrieveClaim(token);

        assertEquals(email,extractedEmail,"Извлеченный email должен совпадать с исходным");
    }

    @Test
    void validateToken_WhenTokenIsTampered_ShouldThrowException(){
        String validToken=jwtUtil.generateToken("test@test.com");
        String temperedToken=validToken+"hacker_attack";

        Executable action=()->jwtUtil.validateTokenAndRetrieveClaim(temperedToken);
        assertThrows(JWTVerificationException.class,action);
    }

    @Test
    void validateToken_WhenTokenIsExpired_ShouldThrowException(){
        String secret="super_secret_key_1234567890_for_testing";
        Date pastDate=Date.from(ZonedDateTime.now().minusDays(1).toInstant());

        String expiredToken = com.auth0.jwt.JWT.create()
                .withSubject("User details")
                .withClaim("email", "test@test.com")
                .withIssuer("amin")
                .withExpiresAt(pastDate) // <-- Срок годности вышел!
                .sign(Algorithm.HMAC256(secret));

        Executable action=()-> jwtUtil.validateTokenAndRetrieveClaim(expiredToken);
        assertThrows(JWTVerificationException.class,action);
    }
}
