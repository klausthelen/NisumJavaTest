package com.nisum.javatest.dto.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateUserPhonesRequest {
    @Valid
    @NotEmpty(message = "Phone List cannot be empty")
    private List<CreateUserPhoneRequest> phones;
}
