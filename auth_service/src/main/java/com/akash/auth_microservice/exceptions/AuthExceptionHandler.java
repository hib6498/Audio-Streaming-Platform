package com.akash.auth_microservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
  }

  @ExceptionHandler(UserIdNotFound.class)
  public ResponseEntity<String> handleUserIdNotFound(UserIdNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handleUserDoesNotExists(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
  }
}
