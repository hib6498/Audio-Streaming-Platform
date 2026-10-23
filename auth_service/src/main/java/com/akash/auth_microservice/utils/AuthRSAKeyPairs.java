package com.akash.auth_microservice.utils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record AuthRSAKeyPairs(RSAPrivateKey privateKey, RSAPublicKey publicKey) {}
