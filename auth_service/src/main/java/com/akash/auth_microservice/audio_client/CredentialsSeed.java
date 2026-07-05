package com.akash.auth_microservice.audio_client;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CredentialsSeed {
  @Autowired ServiceRepository serviceRepository;
  @Autowired PasswordEncoder passwordEncoder;

  @PostConstruct
  private void registerAudioService() {
    String clientId = "audio-service";
    String secret = "WxHdcuRXtW/2QKoA6No99tMGaB4EYwq92YpOV70JeVs=";

    serviceRepository.save(
        new Service().setClientId(clientId).setSecret(passwordEncoder.encode(secret)));
  }
}
