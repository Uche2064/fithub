package com.uche.fithub.utils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uche.fithub.utils.error.Entity404Exception;
import com.uche.fithub.utils.error.FieldErrorException;
import com.uche.fithub.utils.error.TokenRefreshException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.UnexpectedTypeException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@ComponentScan 
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorException> errors = new ArrayList<>();

        for (FieldError fieldError : result.getFieldErrors()) {
            FieldErrorException errorResponse = new FieldErrorException(fieldError.getField(),
                    fieldError.getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
            errors.add(errorResponse);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Entity404Exception> handleEntityNotFoundException(EntityNotFoundException ex) {
        Entity404Exception errorResponse = new Entity404Exception();
        errorResponse.setErrorCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<TokenRefreshException> handleTokenExpiredException(ExpiredJwtException ex) {
        TokenRefreshException errorResponse = new TokenRefreshException();
        errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage("Token has expired");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<FieldErrorException> handleUnexpectedTypeException(UnexpectedTypeException ex) {
        FieldErrorException errorResponse = new FieldErrorException();
        errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setField(ex.getLocalizedMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FieldErrorException> handleDefaultExceptions(Exception ex) {
        FieldErrorException errorResponse = new FieldErrorException();
        errorResponse.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setErrorCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
