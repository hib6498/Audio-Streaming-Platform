package com.akash.auth_microservice.security;

import com.akash.auth_microservice.user.UserService;
import com.akash.auth_microservice.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class UserJWTFilter extends OncePerRequestFilter {
  @Autowired
  @Qualifier("handlerExceptionResolver")
  HandlerExceptionResolver exceptionResolver;

  @Autowired private UserService userDetailsService;
  @Autowired private JWTUtils jwtUtils;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().startsWith("/service/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String jwt = jwtUtils.extractAccessTokenFromRequest(request);

    if (jwt == null || jwt.isBlank()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String userId = jwtUtils.validateTokenAndRetrieveId(jwt, "User");

      UserDetails userDetails = userDetailsService.loadUserById(Long.valueOf(userId));

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authToken);

    } catch (Exception e) {
      exceptionResolver.resolveException(request, response, null, e);
    }

    filterChain.doFilter(request, response);
  }
}
