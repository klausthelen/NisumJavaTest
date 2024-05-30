package com.nisum.javatest.utils;

import com.nisum.javatest.dto.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public class RequestUtils {

    public static ResponseEntity<List<ErrorResponse>> buildValidationErrorResponse(
            final BindingResult bindingResult
    ) {
        return new ResponseEntity<>(
                bindingResult.getAllErrors()
                        .stream()
                        .map(error -> ErrorResponse.builder()
                                .message(error.getDefaultMessage())
                                .build()
                        )
                        .toList(),
                HttpStatus.BAD_REQUEST
        );
    }
}
