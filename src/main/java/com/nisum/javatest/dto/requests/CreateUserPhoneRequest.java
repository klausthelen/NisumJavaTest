package com.nisum.javatest.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class CreateUserPhoneRequest {
    @NotNull(message = "Number cannot be null")
    @Pattern(regexp = "^[0-9]+$", message = "Number must be numeric")
    @JsonProperty("number")
    private String number;
    @NotNull(message = "City Code cannot be null")
    @Pattern(regexp = "^[0-9]+$", message = "City Code must be numeric")
    @JsonProperty("city_code")
    private String cityCode;
    @NotNull(message = "Country Code cannot be null")
    @Pattern(regexp = "^[0-9]+$", message = "Country Code must be numeric")
    @JsonProperty("country_code")
    private String countryCode;
}
