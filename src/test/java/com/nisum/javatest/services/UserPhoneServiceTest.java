package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.CreateUserPhoneRequest;
import com.nisum.javatest.dto.requests.CreateUserPhonesRequest;
import com.nisum.javatest.dto.responses.UserPhoneListResponse;
import com.nisum.javatest.dto.responses.UserPhoneResponse;
import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import com.nisum.javatest.repositories.UserPhoneRepository;
import com.nisum.javatest.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserPhoneServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPhoneRepository userPhoneRepository;

    @InjectMocks
    private UserPhoneService userPhoneService;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE1OTY4OTEzLCJleHAiOjE3MTU5NzAzNTN9" +
            ".o3XwOF1ziZ_Z27Z8dpDsARFu5S7hldtpLEei37RxQ0U";

    private static final String EMAIL = "klaus@thelen.com";

    private static final User USER = User.builder()
            .id(UUID.fromString("5db3a6cc-0b2a-4038-a45e-eb73b33a6789"))
            .name("Klaus Thelen")
            .email(EMAIL)
            .build();


    @BeforeEach
    public void setUp() {
        when(jwtService.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
    }

    @Test
    public void getAllUserPhonesTest() {
        //GIVEN
        when(userPhoneRepository.findAllByUser(USER)).thenReturn(
                List.of(
                        UserPhone.builder()
                                .id(123L)
                                .number("123456789")
                                .cityCode(879)
                                .countryCode(147)
                                .build()
                )
        );

        //WHEN
        final ResponseEntity<UserPhoneListResponse> response = userPhoneService.getAllUserPhones(TOKEN);

        //THEN
        verify(userRepository).findByEmail(EMAIL);
        verify(userPhoneRepository).findAllByUser(USER);
        assertNotNull(response.getBody());
        final UserPhoneListResponse responseBody = response.getBody();
        final UserPhoneResponse firstPhone = responseBody.getPhones().get(0);

        assertEquals("123456789", firstPhone.getNumber());
        assertEquals(879, firstPhone.getCityCode());
        assertEquals(147, firstPhone.getCountryCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void saveUserPhonesTest() {
        //GIVEN
        final CreateUserPhonesRequest request = CreateUserPhonesRequest.builder()
                .phones(
                        List.of(
                                CreateUserPhoneRequest.builder()
                                        .number("123456789")
                                        .cityCode("879")
                                        .countryCode("147")
                                        .build()
                        )
                )
                .build();

        //WHEN
        final ResponseEntity<CreateUserPhonesRequest> response =
                userPhoneService.saveUserPhones(TOKEN, request);

        //THEN
        verify(userRepository).findByEmail(EMAIL);
        verify(userPhoneRepository).save(
                UserPhone.builder()
                        .number("123456789")
                        .cityCode(879)
                        .countryCode(147)
                        .user(USER)
                        .build()
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(request, response.getBody());
    }

}
