package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class HandlerException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> notFound(NotFoundException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ResponseError.builder()
                .error(e.getMessage())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseError> badRequest(BadRequestException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ResponseError.builder()
                .error(e.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseError> conflict(ForbiddenException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(ResponseError.builder()
                .error(e.getMessage())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> methodArgumentNotValid(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = buildErrorMessage(bindingResult);
        log.error("Ошибка вадидации: " + errorMessage);
        return new ResponseEntity<>(ResponseError.builder()
                .error("Ошибка вадидации: " + errorMessage)
                .build(), HttpStatus.BAD_REQUEST);
    }

    private String buildErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }
        return errorMessage.toString().replaceAll("; $", "");
    }
}