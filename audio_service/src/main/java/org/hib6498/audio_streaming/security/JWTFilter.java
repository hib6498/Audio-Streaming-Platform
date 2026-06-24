package org.hib6498.audio_streaming.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.hib6498.audio_streaming.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JWTFilter extends OncePerRequestFilter {
  @Autowired
  @Qualifier("handlerExceptionResolver")
  HandlerExceptionResolver exceptionResolver;

  @Autowired private JWTUtils jwtUtils;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String jwt = jwtUtils.extractJwtFromRequest(request);

    // move onto next filters if no jwt is found
    if (jwt == null || jwt.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      UserInfoEntity userDetails = jwtUtils.validateTokenAndRetrieveUser(jwt);
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (Exception e) {
      exceptionResolver.resolveException(request, response, null, e);
    }

    filterChain.doFilter(request, response);
  }
}
