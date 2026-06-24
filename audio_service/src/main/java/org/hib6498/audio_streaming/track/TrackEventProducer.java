package org.hib6498.audio_streaming.track;

import org.hib6498.audio_streaming.configs.RabbitMQConfig;
import org.hib6498.audio_streaming.track.dtos.TrackProcessEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrackEventProducer {
  private final RabbitTemplate rabbitTemplate;

  public TrackEventProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendTrackProcessEvent(TrackProcessEvent message) {
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.TRACK_EVENTS_EXCHANGE, RabbitMQConfig.TRACK_EVENTS_ROUTING_KEY, message);
  }
}
