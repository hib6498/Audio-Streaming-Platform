package org.hib6498.audio_streaming.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {
  private final RSAKey publicKey;

  public JWTUtils(RSA_KeyUtils rsaKeyUtils)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    publicKey = rsaKeyUtils.getRSAPublicKey();
  }

  public UserInfoEntity validateTokenAndRetrieveUser(String token) throws JWTVerificationException {
    JWTVerifier verifier =
        JWT.require(Algorithm.RSA256(publicKey)).withSubject("User").withIssuer("a-ms").build();
    DecodedJWT jwt = verifier.verify(token);

    UserInfoEntity userInfoEntity = new UserInfoEntity();
    String id = jwt.getClaim("id").asString();
    userInfoEntity.setUserId(Long.valueOf(id));
    return userInfoEntity;
  }

  public String extractJwtFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7); // Remove "Bearer "
    }

    //    if (request.getCookies() != null) {
    //      for (Cookie cookie : request.getCookies()) {
    //        if ("jwtAccessToken".equals(cookie.getName())) {
    //          return cookie.getValue();
    //        }
    //      }
    //    }

    return null;
  }
}
