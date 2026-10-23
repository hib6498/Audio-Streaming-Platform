package com.akash.auth_microservice.utils;

import com.akash.auth_microservice.refreshToken.RefreshToken;
import com.akash.auth_microservice.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {
  private final RSAPrivateKey privateKey;
  private final RSAPublicKey publicKey;

  @Value("${REFRESH_KEY}")
  private String refreshKey;

  @Value("${ACCESS_TOKEN_EXPIRATION}")
  private int accessTokenExpirationInMinutes;

  @Value("${REFRESH_TOKEN_EXPIRATION}")
  private int refreshTokenExpirationInDays;

  public JWTUtils(AuthRSAKeyPairs keyPairs) {
    this.privateKey = keyPairs.privateKey();
    this.publicKey = keyPairs.publicKey();
  }

  public String generateAccessToken(Long userId)
      throws JWTCreationException, IllegalArgumentException {
    return generateAccessToken(userId.toString(), "User").getFirst();
  }

  public Pair<String, Date> generateAccessToken(String username, String subject) {
    Calendar calendar = Calendar.getInstance();
    Date creationDate = calendar.getTime();
    calendar.add(Calendar.MINUTE, accessTokenExpirationInMinutes);
    Date expirationDate = calendar.getTime();

    return Pair.of(
        JWT.create()
            .withSubject(subject)
            .withClaim("id", username)
            .withIssuedAt(creationDate)
            .withIssuer("a-ms")
            .withExpiresAt(expirationDate)
            .sign(Algorithm.RSA256(privateKey)),
        expirationDate);
  }

  public RefreshToken generateRefreshToken(User user, Date issuedAt, Date expiresAt) {
    String token =
        JWT.create()
            .withSubject("refresh")
            .withClaim("id", user.getUserId().toString()) // keep it consistent with access token
            .withIssuedAt(issuedAt)
            .withIssuer("a-ms")
            .withExpiresAt(expiresAt)
            .sign(Algorithm.HMAC256(refreshKey));

    return new RefreshToken()
        .setToken(token)
        .setUser(user)
        .setExpiresAt(Timestamp.from(expiresAt.toInstant()))
        .setRevoked(false)
        .setUser(user);
  }

  public RefreshToken generateRefreshToken(User user) {
    Calendar calendar = Calendar.getInstance();
    Date creationDate = calendar.getTime();
    calendar.add(Calendar.DATE, accessTokenExpirationInMinutes);
    Date expirationDate = calendar.getTime();

    return generateRefreshToken(user, creationDate, expirationDate);
  }

  public RefreshToken verifyAndRotateRefreshToken(RefreshToken token) {
    JWTVerifier verifier =
        JWT.require(Algorithm.HMAC256(refreshKey))
            .withIssuer("a-ms")
            .withClaim("id", token.getUser().getUserId().toString())
            .withSubject("refresh")
            .build();
    DecodedJWT jwt = verifier.verify(token.getToken());

    return generateRefreshToken(token.getUser(), jwt.getIssuedAt(), jwt.getExpiresAt());
  }

  public String validateTokenAndRetrieveId(String token, String subject)
      throws JWTVerificationException {
    JWTVerifier verifier =
        JWT.require(Algorithm.RSA256(publicKey)).withSubject(subject).withIssuer("a-ms").build();
    DecodedJWT jwt = verifier.verify(token);
    return jwt.getClaim("id").asString();
  }

  public String extractAccessTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7); // Remove "Bearer "
    }

    return null;
  }
}
