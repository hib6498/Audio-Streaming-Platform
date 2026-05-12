package com.akash.auth_microservice.refreshToken;

import com.akash.auth_microservice.user.User;
import com.akash.auth_microservice.utils.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
  @Autowired private RefreshTokenRepository refreshTokenRepository;
  @Autowired private JWTUtils jwtUtils;

  private RefreshToken getRefreshTokenInfo(HttpServletRequest request)
      throws RefreshTokenNotFoundException {
    Optional<Cookie> refreshTokenCookie =
        Optional.ofNullable(request.getCookies()).stream()
            .flatMap(Arrays::stream)
            .filter(cookie -> "jwtRT".equals(cookie.getName()))
            .findFirst();

    if (refreshTokenCookie.isEmpty())
      throw new RefreshTokenNotFoundException("No refresh token cookie found");

    Optional<RefreshToken> refreshToken =
        refreshTokenRepository.findById(refreshTokenCookie.get().getValue());
    if (refreshToken.isEmpty()) throw new RefreshTokenNotFoundException("Invalid token");

    return refreshToken.get();
  }

  public RefreshToken generateRefreshToken(User user) {
    RefreshToken refreshToken = jwtUtils.generateRefreshToken(user);
    refreshToken.setUser(user);
    refreshTokenRepository.save(refreshToken);

    return refreshToken;
  }

  // better to use transactional here bcz more than 1 save
  @Transactional
  public RefreshToken validateAndRotateRefreshToken(HttpServletRequest request) {
    RefreshToken savedToken = this.getRefreshTokenInfo(request);
    RefreshToken rotatedToken = jwtUtils.verifyAndRotateRefreshToken(savedToken);

    savedToken.setRevoked(true);
    refreshTokenRepository.save(savedToken);
    return refreshTokenRepository.save(rotatedToken);
  }

  public void removeRefreshTokensForUser(Long userId) {
    refreshTokenRepository.deleteByUser_UserId(userId);
  }
}
