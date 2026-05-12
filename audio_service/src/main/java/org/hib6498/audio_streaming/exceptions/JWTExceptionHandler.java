package org.hib6498.audio_streaming.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JWTExceptionHandler {
  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<String> handleTokenExpired(TokenExpiredException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(JWTVerificationException.class)
  public ResponseEntity<String> handleJWTVerificationError(JWTVerificationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Invalid JWT Token: " + ex.getMessage());
  }
}
