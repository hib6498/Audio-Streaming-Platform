package com.akash.auth_microservice.exceptions;

public class UserIdNotFound extends RuntimeException {
  public UserIdNotFound(String message) {
    super(message);
  }
}
