package com.akash.auth_microservice.user;

import com.akash.auth_microservice.exceptions.UserAlreadyExistsException;
import com.akash.auth_microservice.refreshToken.RefreshToken;
import com.akash.auth_microservice.refreshToken.RefreshTokenService;
import com.akash.auth_microservice.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;
  @Autowired private JWTUtils jwtUtils;
  @Autowired private RefreshTokenService refreshTokenService;
  @Autowired private UserEventProducer producer;

  public User registerUser(String username, String email, String displayName, String password) {
    if (userRepository.existsByUsernameOrEmail(username, email)) {
      throw new UserAlreadyExistsException("username or email already exists");
    }

    User user =
        new User()
            .setUsername(username)
            .setEmail(email)
            .setDisplayName(displayName)
            .setPassword(passwordEncoder.encode(password));

    return userRepository.save(user);
  }

  public Pair<String, RefreshToken> loginAndGenerateTokens(String username, String password) {
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));

    User authenticatedUser = (User) auth.getPrincipal();
    String accessToken = jwtUtils.generateAccessToken(authenticatedUser.getUserId());
    RefreshToken refreshToken = refreshTokenService.generateRefreshToken(authenticatedUser);

    return Pair.of(accessToken, refreshToken);
  }
}
