package com.expensemanagement.splitshare.exception;

import com.expensemanagement.splitshare.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // TODO: Check all thrown exceptions and handled

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException badRequestException, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails("", badRequestException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFoundException(NotFoundException notFoundException, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails("", notFoundException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ErrorDetails> handleUnauthorizedException(UnauthorizedException unauthorizedException, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails("", unauthorizedException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = InternalServerException.class)
    public ResponseEntity<ErrorDetails> handleInternalServerException(InternalServerException internalServerException, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails("", internalServerException.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
