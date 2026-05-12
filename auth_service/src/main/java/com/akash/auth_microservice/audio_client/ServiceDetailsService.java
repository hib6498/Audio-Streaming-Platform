package com.akash.auth_microservice.audio_client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServiceDetailsService implements UserDetailsService {
  @Autowired private ServiceRepository serviceRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return serviceRepository
        .findByClientId(username)
        .orElseThrow(() -> new UsernameNotFoundException("No such service found"));
  }
}
