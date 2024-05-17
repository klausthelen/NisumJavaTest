package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.LoginRequest;
import com.nisum.javatest.dto.responses.CreateUserResponse;
import com.nisum.javatest.dto.responses.ErrorResponse;
import com.nisum.javatest.dto.responses.LoginResponse;
import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import com.nisum.javatest.dto.requests.CreateUserRequest;
import com.nisum.javatest.repositories.UserPhoneRepository;
import com.nisum.javatest.repositories.UserRepository;
import com.nisum.javatest.utils.DateTimeProvider;
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
    public ResponseEntity<?> createUser(final CreateUserRequest user) {
        if(!isValidPassword(user.getPassword())) {
            return new ResponseEntity<>(
                    ErrorResponse.builder()
                            .message("Wrong password format. " + passwordRegexMessage)
                            .build(),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>(
                    ErrorResponse.builder()
                            .message("The email is already registered")
                            .build(),
                    HttpStatus.CONFLICT
            );
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

        return new ResponseEntity<>(
                CreateUserResponse.builder()
                        .id(createdUser.getId())
                        .name(createdUser.getName())
                        .email(createdUser.getEmail())
                        .created(createdUser.getCreated())
                        .modified(createdUser.getModified())
                        .lastLogin(createdUser.getLastLogin())
                        .token(createdUser.getJwtToken())
                        .isActive(createdUser.isActive())
                        .build(),
                HttpStatus.CREATED
        );
    }

    private boolean isValidPassword(final String password) {
        return password.matches(passwordRegex);
    }

    @Transactional
    public ResponseEntity<?> login(final LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException badCredentialsException) {
            return new ResponseEntity<>(
                    ErrorResponse.builder()
                            .message("Invalid email or password")
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }

        final User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        final String token = jwtService.generateToken(user);
        user.setLastLogin(DateTimeProvider.now());
        user.setJwtToken(token);
        userRepository.save(user);

        return new ResponseEntity<>(
                LoginResponse.builder()
                        .token(token)
                        .build(),
                HttpStatus.OK
        );
    }
}
