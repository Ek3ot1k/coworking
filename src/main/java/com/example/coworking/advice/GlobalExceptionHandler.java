package com.example.coworking.advice;

import com.example.coworking.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> handleNotFoundException(ResourceNotFoundException e){
        return Map.of("error",e.getMessage());
    }

    // Ошибки валидации (@Valid) (ошибка 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleValidation(MethodArgumentNotValidException e){
        return e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
    }

    // Ошибки кривого JSON (ошибка 400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleNotReadable(HttpMessageNotReadableException e){
        return Map.of("error","Invalid JSON format");
    }

    // Ошибка бизнес-логики (ошибка 409)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> handleIllegalState(IllegalStateException e){
        return Map.of("error",e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> handleDataIntegrityViolation(DataIntegrityViolationException e){
        return Map.of("error", "Запись с такими уникальными данными уже существует в системе.");
    }
}