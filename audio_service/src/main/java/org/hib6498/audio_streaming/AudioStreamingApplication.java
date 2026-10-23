package org.hib6498.audio_streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AudioStreamingApplication {

  public static void main(String[] args) {
    SpringApplication.run(AudioStreamingApplication.class, args);
  }
}
