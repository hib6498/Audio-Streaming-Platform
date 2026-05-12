package org.hib6498.audio_streaming.configs;

import org.hib6498.audio_streaming.store.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//note: i dont think this will be used anymore
//this was used before rabbitmq was introduced in this project.
//but just in case if queue is busy, these synchronous calls shall be used
@Configuration
public class WebClientConfig {

  @Bean
  public WebClient authWebClient(
      @Value("${AUTH_SERVICE_BASE_URL}") String baseUrl, TokenManager tokenManager) {

    return WebClient.builder()
        .baseUrl(baseUrl)

        // auth filter
        .filter(
            (request, next) ->
                next.exchange(
                    ClientRequest.from(request)
                        .headers(h -> h.setBearerAuth(tokenManager.getToken()))
                        .build()))

        // error logging filter
        .filter(
            (request, next) ->
                next.exchange(request)
                    .flatMap(
                        response -> {
                          if (response.statusCode().isError()) {
                            return response
                                .bodyToMono(String.class)
                                .flatMap(
                                    body -> {
                                      System.out.println(
                                          "WebClient error: "
                                              + response.statusCode()
                                              + " Body: "
                                              + body);

                                      // rebuild response so downstream can still read it
                                      ClientResponse newResponse =
                                          ClientResponse.create(response.statusCode())
                                              .headers(
                                                  headers ->
                                                      headers.addAll(
                                                          response.headers().asHttpHeaders()))
                                              .body(body)
                                              .build();

                                      return Mono.just(newResponse);
                                    });
                          }

                          return Mono.just(response);
                        }))
        .build();
  }

  @Bean
  public WebClient loginWebClient(@Value("${AUTH_SERVICE_BASE_URL}") String baseUrl) {
    return WebClient.builder().baseUrl(baseUrl).build();
  }
}
