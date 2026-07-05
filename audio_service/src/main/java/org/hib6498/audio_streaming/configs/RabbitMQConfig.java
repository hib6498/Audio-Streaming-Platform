package org.hib6498.audio_streaming.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String USER_QUEUE = "user";
  public static final String USER_EXCHANGE = "user.exchange";
  public static final String USER_ROUTING_KEY = "user.info";

  public static final String TRACK_EVENTS_QUEUE = "track.transcode";
  public static final String TRACK_EVENTS_EXCHANGE = "track.events.exchange";
  public static final String TRACK_EVENTS_ROUTING_KEY = "track.events.process";
  public static final String TRACK_COMPLETE_QUEUE = "track.transcode.complete";

  //user queue
  @Bean
  public Queue profileUpdateQueue() {
    return QueueBuilder.durable(USER_QUEUE).quorum().build();
  }

  @Bean
  public TopicExchange userExchange() {
    return ExchangeBuilder.topicExchange(USER_EXCHANGE).build();
  }

  @Bean
  public Binding userBinding(Queue profileUpdateQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(profileUpdateQueue).to(userExchange).with(USER_ROUTING_KEY);
  }

  // track events queue
  @Bean
  public Queue trackEventsQueue() {
    return QueueBuilder.durable(TRACK_EVENTS_QUEUE).quorum().build();
  }

  @Bean
  public TopicExchange trackEventsExchange() {
    return ExchangeBuilder.topicExchange(TRACK_EVENTS_EXCHANGE).build();
  }

  @Bean
  public Binding trackEventsBinding(Queue trackEventsQueue, TopicExchange trackEventsExchange) {
    return BindingBuilder.bind(trackEventsQueue).to(trackEventsExchange).with(TRACK_EVENTS_ROUTING_KEY);
  }

  @Bean
  public Queue trackCompleteQueue() {
    return QueueBuilder.durable(TRACK_COMPLETE_QUEUE).quorum().build();
  }

  // both use json messages
  @Bean
  public MessageConverter converter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    typeMapper.setTrustedPackages("org.hib6498.audio_streaming.track.dtos");
    converter.setJavaTypeMapper(typeMapper);
    return converter;
  }
}
