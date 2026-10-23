package com.akash.auth_microservice.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.akash.auth_microservice.refreshToken.RefreshToken;
import com.akash.auth_microservice.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.test.util.ReflectionTestUtils;

public class JWTUtilsTest {
  private JWTUtils jwtUtils;

  @BeforeEach
  public void setUp() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    AuthRSAKeyPairs authRSAKeyPairs = mock(AuthRSAKeyPairs.class);
    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
    keyGenerator.initialize(2048);
    KeyPair keyPair = keyGenerator.generateKeyPair();

    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    when(authRSAKeyPairs.privateKey()).thenReturn(privateKey);
    when(authRSAKeyPairs.publicKey()).thenReturn(publicKey);

    jwtUtils = new JWTUtils(authRSAKeyPairs);
    ReflectionTestUtils.setField(jwtUtils, "refreshKey", "test-refresh-key");
    ReflectionTestUtils.setField(jwtUtils, "accessTokenExpirationInMinutes", 15);
    ReflectionTestUtils.setField(jwtUtils, "refreshTokenExpirationInDays", 7);
  }

  @Test
  void generateAccessToken_shouldReturnValidAccessToken()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    Pair<String, Date> token = jwtUtils.generateAccessToken("test", "User");

    assertNotNull(token);

    DecodedJWT jwt = JWT.decode(token.getFirst());

    // Compare at second-level precision, since JWT 'exp' claim only stores seconds
    long expectedEpochSeconds = token.getSecond().toInstant().getEpochSecond();
    long actualEpochSeconds = jwt.getExpiresAt().toInstant().getEpochSecond();
    assertEquals(expectedEpochSeconds, actualEpochSeconds);
    assertTrue(jwt.getExpiresAt().after(new Date()));
  }

  @Test
  void generateRefreshToken_shouldReturnValidRefreshToken()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    User user = new User().setUserId(123L);
    RefreshToken token = jwtUtils.generateRefreshToken(user);

    assertNotNull(token);
    DecodedJWT jwt = JWT.decode(token.getToken());
    assertEquals(user.getUserId().toString(), jwt.getClaim("id").asString());
    assertTrue(jwt.getExpiresAt().after(new Date()));
  }
}
