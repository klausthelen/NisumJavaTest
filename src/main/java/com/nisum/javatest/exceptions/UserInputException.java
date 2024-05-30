package com.nisum.javatest.exceptions;

import com.nisum.javatest.dto.responses.ErrorResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class UserInputException extends RuntimeException{

    private ErrorResponse errorResponse;
    private HttpStatus httpStatus;
}
