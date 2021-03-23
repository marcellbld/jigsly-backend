package com.mbld.jigsly.exception;

import com.mbld.jigsly.exception.domain.EmailExistException;
import com.mbld.jigsly.exception.domain.RoomFullException;
import com.mbld.jigsly.exception.domain.UserNotFoundException;
import com.mbld.jigsly.exception.domain.UsernameExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDeniedException() {
        return createResponse(FORBIDDEN, "Not enough permission");
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<String> emailExistException(EmailExistException exception) {
        return createResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<String> usernameExistException(UsernameExistException exception) {
        return createResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> userNotFoundException(UserNotFoundException exception) {
        return createResponse(BAD_REQUEST, exception.getMessage());
    }
    private ResponseEntity<String> createResponse(HttpStatus status, String message){
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(RoomFullException.class)
    public ResponseEntity<String> roomIsFull(RoomFullException exception) {
        return createResponse(BAD_REQUEST, exception.getMessage());
    }

}
