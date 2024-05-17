package com.nisum.javatest.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
public class CreateUserResponse {

    private UUID id;
    private String name;
    private String email;
    private ZonedDateTime created;
    private ZonedDateTime modified;
    @JsonProperty("last_login")
    private ZonedDateTime lastLogin;
    private String token;
    private boolean isActive;
}
