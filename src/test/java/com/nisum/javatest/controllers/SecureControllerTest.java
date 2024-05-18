package com.nisum.javatest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.javatest.dto.requests.CreateUserPhoneRequest;
import com.nisum.javatest.dto.requests.CreateUserPhonesRequest;
import com.nisum.javatest.dto.responses.UserPhoneListResponse;
import com.nisum.javatest.dto.responses.UserPhoneResponse;
import com.nisum.javatest.services.UserPhoneService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class SecureControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserPhoneService userPhoneService;

    @InjectMocks
    private SecureController secureController;

    private static final String RAW_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE1OTY4OTEzLCJleHAiOjE3MTU5NzAzNTN9" +
            ".o3XwOF1ziZ_Z27Z8dpDsARFu5S7hldtpLEei37RxQ0U";

    private static final String TOKEN ="eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE1OTY4OTEzLCJleHAiOjE3MTU5NzAzNTN9" +
            ".o3XwOF1ziZ_Z27Z8dpDsARFu5S7hldtpLEei37RxQ0U";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(secureController).build();
    }

    @Test
    public void getUserPhoneListTest() throws Exception {
        //GIVEN

        when(userPhoneService.getAllUserPhones(TOKEN)).thenReturn(
                new ResponseEntity<>(UserPhoneListResponse.builder()
                        .phones(
                                List.of(
                                        UserPhoneResponse.builder()
                                                .number("312321")
                                                .cityCode(123)
                                                .countryCode(456)
                                                .build()
                                )
                        ).build(),
                        HttpStatus.OK
                )
        );

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(get("/api/secured/v1/phone_list")
                                .header("Authorization", RAW_TOKEN))
                        .andReturn()
                        .getResponse();

        //THEN.
        verify(userPhoneService).getAllUserPhones(TOKEN);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(
                "{\"phones\":[{\"number\":\"312321\",\"city_code\":123,\"country_code\":456}]}",
                response.getContentAsString()
        );
    }

    @Test
    public void addUserPhoneListTestSuccess() throws Exception {
        //GIVEN
        final CreateUserPhonesRequest request = CreateUserPhonesRequest.builder()
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

        when(userPhoneService.saveUserPhones(eq(TOKEN), any(CreateUserPhonesRequest.class)))
                .thenReturn(new ResponseEntity<>(request, HttpStatus.CREATED));

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(post("/api/secured/v1/add_phone")
                        .header("Authorization", RAW_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andReturn().getResponse();

        //THEN
        verify(userPhoneService).saveUserPhones(eq(TOKEN), any(CreateUserPhonesRequest.class));
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(
                "{\"phones\":[{\"number\":\"312321\",\"city_code\":\"123\",\"country_code\":\"456\"}]}",
                response.getContentAsString()
        );
    }

    @Test
    public void addUserPhoneListTestBindError() throws Exception {
        //GIVEN
        final CreateUserPhonesRequest request = CreateUserPhonesRequest.builder()
                .phones(
                        List.of(
                                CreateUserPhoneRequest.builder()
                                        .number("312321")
                                        .cityCode("123")
                                        .build()
                        )
                )
                .build();

        //WHEN
        final MockHttpServletResponse response =
                mockMvc.perform(post("/api/secured/v1/add_phone")
                                .header("Authorization", RAW_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                        .andReturn().getResponse();

        //THEN
        verifyNoInteractions(userPhoneService);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(
                "[{\"message\":\"Country Code cannot be null\"}]",
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
