package org.hib6498.audio_streaming.utils;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RSA_KeyUtils {
  @Value("classpath:public.pem")
  private Resource publicKeyResource;

  public RSAKey getRSAPublicKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String keyFile = new String(publicKeyResource.getInputStream().readAllBytes());

    String beginStr = "-----BEGIN PUBLIC KEY-----";
    String endStr = "-----END PUBLIC KEY-----";
    String key =
        keyFile.replace(beginStr, "").replaceAll(System.lineSeparator(), "").replace(endStr, "");

    byte[] encoded = Base64.getDecoder().decode(key);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
    return (RSAKey) keyFactory.generatePublic(keySpec);
  }
}
