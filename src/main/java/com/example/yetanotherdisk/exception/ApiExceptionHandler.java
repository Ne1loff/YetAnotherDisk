package com.example.yetanotherdisk.exception;

import com.example.yetanotherdisk.exception.badRequest.BadRequestException;
import com.example.yetanotherdisk.exception.badRequest.ValidationError;
import com.example.yetanotherdisk.exception.notFound.NotFoundException;
import com.fasterxml.jackson.core.JacksonException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    ResponseEntity<Error> handleNotFoundException(@NotNull NotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Error error = new Error(status.value(), exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    ResponseEntity<Error> handleBadRequestException(@NotNull BadRequestException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Error error = new Error(status.value(), exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(value = {TypeMismatchException.class})
    ResponseEntity<Error> handleTypeMismatchException(@NotNull TypeMismatchException e) {
        return handleBadRequestException(new ValidationError());
    }

    @ExceptionHandler(value = {JacksonException.class})
    ResponseEntity<Error> handleJacksonException(@NotNull JacksonException e) {
        return handleBadRequestException(new ValidationError());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    ResponseEntity<Error> handleJacksonException(@NotNull HttpMessageNotReadableException e) {
        return handleBadRequestException(new ValidationError());
    }

}
