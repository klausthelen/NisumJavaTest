package com.nisum.javatest.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserPhoneResponse {
    private String number;
    @JsonProperty("city_code")
    private Integer cityCode;
    @JsonProperty("country_code")
    private Integer countryCode;
}
