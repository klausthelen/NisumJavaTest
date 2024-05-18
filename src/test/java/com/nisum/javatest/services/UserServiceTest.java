package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.CreateUserPhoneRequest;
import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.dto.requests.LoginRequest;
import com.nisum.javatest.dto.responses.CreateUserResponse;
import com.nisum.javatest.dto.responses.ErrorResponse;
import com.nisum.javatest.dto.responses.LoginResponse;
import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import com.nisum.javatest.repositories.UserPhoneRepository;
import com.nisum.javatest.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPhoneRepository userPhoneRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Value("${password.regex}")
    private String passwordRegex;

    @Value("${password.regex.message}")
    private String passwordRegexMessage;

    private static final String TOKEN ="eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE1OTY4OTEzLCJleHAiOjE3MTU5NzAzNTN9" +
            ".o3XwOF1ziZ_Z27Z8dpDsARFu5S7hldtpLEei37RxQ0U";


    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(userService, "passwordRegex", passwordRegex);
        ReflectionTestUtils.setField(userService, "passwordRegexMessage", passwordRegexMessage);
    }

    @Test
    public void whenCreateUserWithValidDataThenCreateUser() {
        // GIVEN
        final CreateUserRequest request = CreateUserRequest.builder()
                .name("Klaus Thelen")
                .email("klaus@thelen.com")
                .password("PassWord%456")
                .phones(
                        List.of(
                                CreateUserPhoneRequest.builder()
                                        .number("312321")
                                        .cityCode("123")
                                        .countryCode("456")
                                        .build()
                        )
                )
                .build();

        final User createdUser = User.builder()
                .id(UUID.fromString("5db3a6cc-0b2a-4038-a45e-eb73b33a6789"))
                .name("Klaus Thelen")
                .email("klaus@thelen.com")
                .password("encryptedPass")
                .jwtToken(TOKEN)
                .created(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .modified(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .lastLogin(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .isActive(true)
                .build();

        when(userRepository.findByEmail("klaus@thelen.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("PassWord%456")).thenReturn("encryptedPass");
        when(jwtService.generateToken(any(User.class))).thenReturn(TOKEN);
        when(userRepository.save(any(User.class))).thenReturn(createdUser);

        // WHEN
        final ResponseEntity<?> response = userService.createUser(request);

        // THEN
        verify(userRepository).findByEmail("klaus@thelen.com");
        verify(userRepository).save(any(User.class));
        verify(userPhoneRepository).save(any(UserPhone.class));
        assertNotNull(response);
        final CreateUserResponse responseBody = (CreateUserResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals(UUID.fromString("5db3a6cc-0b2a-4038-a45e-eb73b33a6789"), responseBody.getId());
        assertEquals("Klaus Thelen", responseBody.getName());
        assertEquals("klaus@thelen.com", responseBody.getEmail());
        assertEquals(TOKEN, responseBody.getToken());
        assertTrue(responseBody.isActive());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void whenCreateUserWithInvalidPasswordThenReturnMessageError() {
        // GIVEN
        final CreateUserRequest request = CreateUserRequest.builder()
                .name("Klaus Thelen")
                .email("klaus@thelen.com")
                .password("password")
                .phones(
                        List.of(
                                CreateUserPhoneRequest.builder()
                                        .number("312321")
                                        .cityCode("123")
                                        .countryCode("456")
                                        .build()
                        )
                )
                .build();

        // WHEN
        final ResponseEntity<?> response = userService.createUser(request);

        // THEN
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userPhoneRepository);
        assertNotNull(response);
        final ErrorResponse responseBody = (ErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Wrong password format. " + passwordRegexMessage, responseBody.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    public void whenCreateUserWithExistingEmailThenReturnMessageError() {
        //GIVEN
        final CreateUserRequest request = CreateUserRequest.builder()
                .name("Klaus Thelen")
                .email("klaus@thelen.com")
                .password("PassWord%456")
                .phones(
                        List.of(
                                CreateUserPhoneRequest.builder()
                                        .number("312321")
                                        .cityCode("123")
                                        .countryCode("456")
                                        .build()
                        )
                )
                .build();
        final User user = User.builder()
                .id(UUID.fromString("5db3a6cc-0b2a-4038-a45e-eb73b33a6789"))
                .email("klaus@thelen.com")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //WHEN
        final ResponseEntity<?> response = userService.createUser(request);

        // THEN
        verify(userRepository).findByEmail("klaus@thelen.com");
        verifyNoMoreInteractions(userPhoneRepository);
        verifyNoInteractions(userPhoneRepository);
        assertNotNull(response);
        final ErrorResponse responseBody = (ErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("The email is already registered", responseBody.getMessage());
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testLoginSuccess() {
        //GIVEN
        final LoginRequest request = LoginRequest.builder()
                .email("klaus@thelen.com")
                .password("PassWord%456")
                .build();

        final User user = User.builder()
                .email("klaus@thelen.com")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        //WHEN
        final  ResponseEntity<?> response = userService.login(request);

        //THEN
        verify(userRepository).findByEmail("klaus@thelen.com");
        assertNotNull(response);
        final LoginResponse responseBody = (LoginResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals(TOKEN, responseBody.getToken());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testLoginFailure() {
        //GIVEN
        final LoginRequest request = LoginRequest.builder()
                .email("klaus@thelen.com")
                .password("PassWord%456")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        //WHEN
        final  ResponseEntity<?> response = userService.login(request);

        //THEN
        verifyNoInteractions(userRepository);
        assertNotNull(response);
        final ErrorResponse responseBody = (ErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid email or password", responseBody.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


}
