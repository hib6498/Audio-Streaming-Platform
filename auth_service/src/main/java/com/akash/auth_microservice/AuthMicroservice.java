package com.akash.auth_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AuthMicroservice {

  public static void main(String[] args) {
    System.out.println("Working dir: " + System.getProperty("user.dir"));
    SpringApplication.run(AuthMicroservice.class, args);
  }
}
