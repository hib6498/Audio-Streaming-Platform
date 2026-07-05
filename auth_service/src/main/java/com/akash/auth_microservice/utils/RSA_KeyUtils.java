package com.akash.auth_microservice.utils;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RSA_KeyUtils {
  @Value("classpath:private.pem")
  private Resource privateKeyResource;

  @Value("classpath:public.pem")
  private Resource publicKeyResource;

  public RSAKey getRSAPrivateKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String keyFile = new String(privateKeyResource.getInputStream().readAllBytes());

    String beginStr = "-----BEGIN PRIVATE KEY-----";
    String endStr = "-----END PRIVATE KEY-----";
    String key =
        keyFile
            .replace(beginStr, "")
            .replace(endStr, "")
            .replaceAll("\\s+", ""); // ← handles \r\n, \n, spaces regardless of OS

    byte[] encoded = Base64.getDecoder().decode(key);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    return (RSAKey) keyFactory.generatePrivate(keySpec);
  }

  public RSAKey getRSAPublicKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String keyFile = new String(publicKeyResource.getInputStream().readAllBytes());

    String beginStr = "-----BEGIN PUBLIC KEY-----";
    String endStr = "-----END PUBLIC KEY-----";
    String key =
        keyFile.replace(beginStr, "").replace(endStr, "").replaceAll("\\s+", ""); // ← same fix here

    byte[] encoded = Base64.getDecoder().decode(key);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    return (RSAKey) keyFactory.generatePublic(keySpec);
  }
}
