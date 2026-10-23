package org.hib6498.audio_streaming.utils;

import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rsa")
public record AuthRSAPublicKey(RSAPublicKey publicKey) {}
