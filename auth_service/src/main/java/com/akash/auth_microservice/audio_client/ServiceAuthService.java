package com.akash.auth_microservice.audio_client;

import com.akash.auth_microservice.utils.JWTUtils;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class ServiceAuthService {
  @Autowired
  @Qualifier("serviceAuthManager")
  AuthenticationManager authenticationManager;

  @Autowired private JWTUtils jwtUtils;

  public Pair<String, Date> loginAndGenerateTokens(String id, String secret) {
    Authentication auth =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, secret));

    com.akash.auth_microservice.audio_client.Service authenticatedUser =
        (com.akash.auth_microservice.audio_client.Service) auth.getPrincipal();

    return jwtUtils.generateAccessToken(authenticatedUser.getClientId(), "service");
  }
}
