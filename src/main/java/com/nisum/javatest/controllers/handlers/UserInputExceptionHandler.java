package com.nisum.javatest.controllers.handlers;

import com.nisum.javatest.dto.responses.ErrorResponse;
import com.nisum.javatest.exceptions.UserInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserInputExceptionHandler {

    @ExceptionHandler(UserInputException.class)
    public ResponseEntity<ErrorResponse> handleUserInputException(final UserInputException exception) {
        return new ResponseEntity<>(
                exception.getErrorResponse(),
                exception.getHttpStatus()
        );
    }
}
