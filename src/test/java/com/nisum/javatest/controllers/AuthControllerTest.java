package com.nisum.javatest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.javatest.dto.requests.CreateUserPhoneRequest;
import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.dto.requests.LoginRequest;
import com.nisum.javatest.dto.responses.CreateUserResponse;
import com.nisum.javatest.dto.responses.LoginResponse;
import com.nisum.javatest.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private static final String TOKEN ="eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE1OTY4OTEzLCJleHAiOjE3MTU5NzAzNTN9" +
            ".o3XwOF1ziZ_Z27Z8dpDsARFu5S7hldtpLEei37RxQ0U";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void loginUserSuccess() throws Exception {
        //GIVEN
        final LoginRequest request = LoginRequest.builder()
                .email("klaus@thelen.com")
                .password("PassWord%456")
                .build();

        final LoginResponse serviceResponse = LoginResponse.builder()
                .token(TOKEN)
                .build();

        doReturn(new ResponseEntity<>(serviceResponse, HttpStatus.OK))
                .when(userService).login(any(LoginRequest.class));

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                        .andReturn().getResponse();

        //THEN
        verify(userService).login(any(LoginRequest.class));
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(
                "{\"token\":\"" + TOKEN +"\"}",
                response.getContentAsString()
        );
    }



    @Test
    public void createUserSuccess() throws Exception {
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

        final CreateUserResponse serviceResponse = CreateUserResponse.builder()
                .id(UUID.fromString("5db3a6cc-0b2a-4038-a45e-eb73b33a6789"))
                .name("Klaus Thelen")
                .email("klaus@thelen.com")
                .created(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .modified(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .lastLogin(ZonedDateTime.parse("2024-05-15T05:00:00.000Z"))
                .token(TOKEN)
                .isActive(true)
                .build();

        doReturn(new ResponseEntity<>(serviceResponse, HttpStatus.CREATED))
                .when(userService).createUser(any(CreateUserRequest.class));

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andReturn().getResponse();

        //THEN
        verify(userService).createUser(any(CreateUserRequest.class));
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(
                "{\"id\":\"5db3a6cc-0b2a-4038-a45e-eb73b33a6789\"," +
                        "\"name\":\"Klaus Thelen\"," +
                        "\"email\":\"klaus@thelen.com\"," +
                        "\"created\":1715749200.000000000," +
                        "\"modified\":1715749200.000000000," +
                        "\"token\":\"" + TOKEN +"\"," +
                        "\"active\":true," +
                        "\"last_login\":1715749200.000000000}",
                response.getContentAsString()
        );
    }

    @Test
    public void createUserBindError() throws Exception {
        //GIVEN
        final CreateUserRequest request = CreateUserRequest.builder()
                .email("klausthelen.com")
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

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                        .andReturn().getResponse();

        //THEN
        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(
                "[{\"message\":\"Email should be valid\"},{\"message\":\"Name cannot be null\"}]",
                response.getContentAsString()
        );
    }


    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
