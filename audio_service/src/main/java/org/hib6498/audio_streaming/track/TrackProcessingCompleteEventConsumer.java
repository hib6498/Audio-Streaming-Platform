package org.hib6498.audio_streaming.track;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hib6498.audio_streaming.track.dtos.TrackProcessingCompleteEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackProcessingCompleteEventConsumer {
  @Autowired private TrackService trackService;

  @RabbitListener(queues = "track.transcode.complete")
  public void markTrackReady(TrackProcessingCompleteEvent message) {
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    try {
      System.out.println("Track event: " + writer.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      System.out.println("couldn't convert class with id: " + message.trackId());
    }

    trackService.markCompleteTrackUpload(message.trackId(), message.s3Key());
  }
}
