package org.hib6498.audio_streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AudioStreamingApplication {

  public static void main(String[] args) {
    SpringApplication.run(AudioStreamingApplication.class, args);
  }

  @GetMapping("/hello")
  public String test() {
    return "hello world";
  }
}
