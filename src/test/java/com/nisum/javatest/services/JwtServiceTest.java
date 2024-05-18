package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private final String EMAIL = "klaus@thelen.com";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
    }

    @Test
    public void generateTokenForCreateUserRequest() {
        //GIVEN
        final CreateUserRequest user = CreateUserRequest.builder()
                .email(EMAIL)
                .build();

        //WHEN
        final String token = jwtService.generateToken(user);

        //THEN
        assertNotNull(token);
        assertEquals(145,token.length());
    }

    @Test
    public void generateTokenForUser() {
        //GIVEN
        final User user = User.builder()
                .email(EMAIL)
                .build();

        //WHEN
        final String token = jwtService.generateToken(user);

        //THEN
        assertNotNull(token);
        assertEquals(145,token.length());
    }

    @Test
    public void getUsernameFromToken() {
        //GIVEN
        final User user = User.builder()
                .email(EMAIL)
                .build();

        final String token = jwtService.generateToken(user);

        //WHEN
        final String username = jwtService.getUsernameFromToken(token);

        //THEN
        assertEquals(EMAIL, username);
    }

    @Test
    public void isTokenValid() {
        //GIVEN
        final UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(EMAIL);

        final User user = User.builder()
                .email(EMAIL)
                .build();

        final String token = jwtService.generateToken(user);

        //WHEN
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        //THEN
        assertTrue(isValid);
    }

}
