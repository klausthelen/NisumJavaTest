package com.nisum.javatest.controllers;

import com.nisum.javatest.dto.requests.LoginRequest;
import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.dto.responses.CreateUserResponse;
import com.nisum.javatest.dto.responses.LoginResponse;
import com.nisum.javatest.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nisum.javatest.utils.RequestUtils.buildValidationErrorResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            final @Valid @RequestBody CreateUserRequest user,
            final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }
        return new ResponseEntity<>(
                userService.createUser(user),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            final @Valid @RequestBody LoginRequest loginRequest,
            final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }
        return new ResponseEntity<>(
                userService.login(loginRequest),
                HttpStatus.OK
        );
    }
}
