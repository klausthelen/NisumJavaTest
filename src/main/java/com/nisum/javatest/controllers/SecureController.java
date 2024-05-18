package com.nisum.javatest.controllers;

import com.nisum.javatest.dto.requests.CreateUserPhonesRequest;
import com.nisum.javatest.dto.responses.UserPhoneListResponse;
import com.nisum.javatest.services.UserPhoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import static com.nisum.javatest.utils.RequestUtils.buildValidationErrorResponse;

@RestController
@RequestMapping("/api/secured/v1")
@RequiredArgsConstructor
public class SecureController {

    private final UserPhoneService userPhoneService;

    @GetMapping("/phone_list")
    public ResponseEntity<UserPhoneListResponse> getUserPhoneList(
            @RequestHeader("Authorization") final String bearerToken
    ) {
        return userPhoneService.getAllUserPhones(bearerToken.substring(7));
    }

    @PostMapping("/add_phone")
    public ResponseEntity<?> addUserPhoneList(
            @RequestHeader("Authorization") final String bearerToken,
            final @Valid @RequestBody CreateUserPhonesRequest request,
            final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }
        return userPhoneService.saveUserPhones(
                bearerToken.substring(7),
                request
        );
    }


}
