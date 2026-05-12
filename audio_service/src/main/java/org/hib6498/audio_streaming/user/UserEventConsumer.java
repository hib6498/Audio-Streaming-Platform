package org.hib6498.audio_streaming.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.respositories.UserInfoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {
  @Autowired private UserInfoRepository userInfoRepository;

  @RabbitListener(queues = "user")
  public void saveUserInfo(UserInfoEntity userInfo) {
    ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
    try {
      System.out.println("User event: " + writer.writeValueAsString(userInfo));
    } catch (JsonProcessingException e) {
      System.out.println("couldn't convert class with id: " + userInfo.getUserId());
    }

    userInfoRepository.save(userInfo);
  }
}
