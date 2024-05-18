package com.nisum.javatest.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
