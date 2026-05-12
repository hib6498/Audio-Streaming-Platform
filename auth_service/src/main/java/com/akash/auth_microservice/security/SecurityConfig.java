package com.akash.auth_microservice.security;

import com.akash.auth_microservice.audio_client.ServiceDetailsService;
import com.akash.auth_microservice.user.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final UserService uds;
  private final ServiceDetailsService sds;
  private final UserJWTFilter userJWTFilter;
  private final ServiceJWTFilter serviceJWTFilter;

  public SecurityConfig(
      UserService uds,
      ServiceDetailsService sds,
      UserJWTFilter userJWTFilter,
      ServiceJWTFilter serviceJWTFilter) {
    this.uds = uds;
    this.sds = sds;
    this.userJWTFilter = userJWTFilter;
    this.serviceJWTFilter = serviceJWTFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean("userAuthManager")
  @Primary
  public AuthenticationManager userAuthManager() {
    return new ProviderManager(List.of(userAuthProvider()));
  }

  @Bean("serviceAuthManager")
  public AuthenticationManager serviceAuthManager() {
    return new ProviderManager(List.of(serviceAuthProvider()));
  }

  @Bean("userAuthProvider")
  @Primary
  public AuthenticationProvider userAuthProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
    provider.setPasswordEncoder(passwordEncoder());

    return provider;
  }

  @Bean("serviceAuthProvider")
  public AuthenticationProvider serviceAuthProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(sds);
    provider.setPasswordEncoder(passwordEncoder());

    return provider;
  }

  @Bean
  @Order(1)
  public SecurityFilterChain serviceFilterChain(
      HttpSecurity http, @Qualifier("serviceAuthManager") AuthenticationManager serviceAuthManager)
      throws Exception {

    return http.securityMatcher("/service/**")
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/service/login").permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationManager(serviceAuthManager)
        .addFilterBefore(serviceJWTFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain userFilterChain(
      HttpSecurity http, @Qualifier("userAuthManager") AuthenticationManager userAuthManager)
      throws Exception {

    return http.securityMatcher("/user/**")
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/user/me", "/user/update/**", "/user/logout")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationManager(userAuthManager)
        .addFilterBefore(userJWTFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
