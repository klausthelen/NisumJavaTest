package com.nisum.javatest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CreateUserRequest {

    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must be a text")
    private String name;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Password cannot be null")
    private String password;
    @NotEmpty(message = "Phone List cannot be empty")
    @Valid
    private List<CreateUserPhoneRequest> phones;
}
