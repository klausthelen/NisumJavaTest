package com.nisum.javatest.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserPhoneListResponse {
    private List<UserPhoneResponse> phones;
}
