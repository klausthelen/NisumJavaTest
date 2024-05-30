package com.nisum.javatest.services;

import com.nisum.javatest.dto.requests.CreateUserPhonesRequest;
import com.nisum.javatest.dto.responses.UserPhoneListResponse;
import com.nisum.javatest.dto.responses.UserPhoneResponse;
import com.nisum.javatest.models.User;
import com.nisum.javatest.models.UserPhone;
import com.nisum.javatest.repositories.UserPhoneRepository;
import com.nisum.javatest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserPhoneService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserPhoneRepository userPhoneRepository;


    public UserPhoneListResponse getAllUserPhones(final String token) {
        return UserPhoneListResponse.builder()
                .phones( userPhoneRepository.findAllByUser(getUserByToken(token)).stream().map(
                                userPhone ->
                                        UserPhoneResponse.builder()
                                                .number(userPhone.getNumber())
                                                .cityCode(userPhone.getCityCode())
                                                .countryCode(userPhone.getCountryCode())
                                                .build()
                        )
                        .toList())
                .build();
    }

    public CreateUserPhonesRequest saveUserPhones(final String token,
                                                  final CreateUserPhonesRequest request) {

        request.getPhones().forEach(
                phone -> userPhoneRepository.save(
                        UserPhone.builder()
                                .number(phone.getNumber())
                                .cityCode(Integer.parseInt(phone.getCityCode()))
                                .countryCode(Integer.parseInt(phone.getCountryCode()))
                                .user(getUserByToken(token))
                                .build()
                )
        );

        return request;

    }

    private User getUserByToken(final String token) {
        final String email = jwtService.getUsernameFromToken(token);
        return userRepository.findByEmail(email).orElseThrow();
    }
}
