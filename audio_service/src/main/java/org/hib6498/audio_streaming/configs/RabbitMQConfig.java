package org.hib6498.audio_streaming.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String QUEUE = "user";
  public static final String EXCHANGE = "user.exchange";
  public static final String ROUTING_KEY = "user.info";

  @Bean
  public Queue profileUpdateQueue() {
    return QueueBuilder.durable(QUEUE).quorum().build();
  }

  @Bean
  public TopicExchange exchange() {
    return ExchangeBuilder.topicExchange(EXCHANGE).build();
  }

  @Bean
  public Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
  }

  @Bean
  public MessageConverter converter() {
    return new Jackson2JsonMessageConverter();
  }
}
