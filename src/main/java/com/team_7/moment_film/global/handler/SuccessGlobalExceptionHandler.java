package com.team_7.moment_film.global.handler;

import com.team_7.moment_film.global.dto.CustomResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SuccessGlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public CustomResponseEntity<String> ExceptionHandler(IllegalArgumentException ex){
        return CustomResponseEntity.errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
