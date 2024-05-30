package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.LoginRequest;
import com.nisum.javatest.dto.responses.CreateUserResponse;
import com.nisum.javatest.dto.responses.ErrorResponse;
import com.nisum.javatest.dto.responses.LoginResponse;
import com.nisum.javatest.exceptions.UserInputException;
import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.repositories.UserPhoneRepository;
import com.nisum.javatest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserPhoneRepository userPhoneRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${password.regex}")
    private String passwordRegex;

    @Value("${password.regex.message}")
    private String passwordRegexMessage;

    @Transactional
    public CreateUserResponse createUser(final CreateUserRequest user) {
        if(!isValidPassword(user.getPassword())) {
            throw UserInputException.builder()
                    .errorResponse(
                            ErrorResponse.builder()
                                    .message("Wrong password format. " + passwordRegexMessage)
                                    .build()
                    )
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .build();
        }

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw UserInputException.builder()
                    .errorResponse(
                            ErrorResponse.builder()
                                    .message("The email is already registered")
                                    .build()
                    )
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }

        final User createdUser = userRepository.save(
                User.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .password(passwordEncoder.encode(user.getPassword()))
                        .jwtToken(jwtService.generateToken(user))
                        .isActive(true)
                        .build());

        user.getPhones().forEach(
                phone -> userPhoneRepository.save(
                        UserPhone.builder()
                                .number(phone.getNumber())
                                .cityCode(Integer.parseInt(phone.getCityCode()))
                                .countryCode(Integer.parseInt(phone.getCountryCode()))
                                .user(createdUser)
                                .build()
                )
        );

        return CreateUserResponse.builder()
                .id(createdUser.getId())
                .name(createdUser.getName())
                .email(createdUser.getEmail())
                .created(createdUser.getCreated())
                .modified(createdUser.getModified())
                .lastLogin(createdUser.getLastLogin())
                .token(createdUser.getJwtToken())
                .isActive(createdUser.isActive())
                .build();
    }

    @Transactional
    public LoginResponse login(final LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (final BadCredentialsException badCredentialsException) {
            throw UserInputException.builder()
                    .errorResponse(
                            ErrorResponse.builder()
                                    .message("Invalid email or password")
                                    .build()
                    )
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        final User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        final String token = jwtService.generateToken(user);
        user.setLastLogin(ZonedDateTime.now());
        user.setJwtToken(token);
        userRepository.save(user);

        return LoginResponse.builder()
                .token(token)
                .build();
    }

    private boolean isValidPassword(final String password) {
        return password.matches(passwordRegex);
    }
}
