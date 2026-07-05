package com.akash.auth_microservice.audio_client;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, String> {
  Optional<Service> findByClientId(String clientId);
}
