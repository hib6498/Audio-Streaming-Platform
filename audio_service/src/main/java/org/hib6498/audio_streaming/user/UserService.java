package org.hib6498.audio_streaming.user;

import java.util.*;
import org.hib6498.audio_streaming.entities.UserInfoEntity;
import org.hib6498.audio_streaming.respositories.TrackUserRepository;
import org.hib6498.audio_streaming.respositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserService {
  @Autowired private WebClient authWebClient;
  @Autowired private UserInfoRepository userInfoRepository;
  @Autowired private TrackUserRepository trackUserRepository;

  public List<UserInfoDTO> getArtists() {
    return userInfoRepository.getAllArtists();
  }

  public ManyUsersResponseDTO getManyUserInfoFromAuthService(List<Long> userIds) {
    return authWebClient
        .post()
        .uri("/service/users")
        .bodyValue(Map.of("ids", userIds))
        .retrieve()
        .bodyToMono(ManyUsersResponseDTO.class)
        .block();
  }

  public List<UserInfoEntity> gatherUsersInfo(List<Long> ids) {
    List<UserInfoEntity> availableInfo = getManyUsersInfo(ids);

    if (availableInfo.size() == ids.size()) return availableInfo;

    Set<Long> required = new HashSet<>(ids);
    for (UserInfoEntity e : availableInfo) {
      required.remove(e.getUserId());
    }

    ManyUsersResponseDTO callForInfo = getManyUserInfoFromAuthService(required.stream().toList());
    userInfoRepository.saveAll(callForInfo.users());

    availableInfo.addAll(callForInfo.users());
    return availableInfo;
  }

  public List<UserInfoEntity> getManyUsersInfo(List<Long> ids) {
    return userInfoRepository.findAllById(ids);
  }

  public UserInfoEntity populateUserInfoObject(UserInfoEntity partialInfo) {
    Optional<UserInfoEntity> user = userInfoRepository.findById(partialInfo.getUserId());
    if (user.isPresent()) return user.get();

    UserInfoEntity requestedInfo = getUserInfoFromAuthService(partialInfo.getUserId());

    return userInfoRepository.save(requestedInfo);
  }

  public UserInfoEntity getUserInfoFromAuthService(Long userId) {
    Map<String, Long> pathParam = new HashMap<>(1);
    pathParam.put("id", userId);

    return authWebClient
        .get()
        .uri("/service/user/{id}", pathParam)
        .retrieve()
        .bodyToMono(UserInfoEntity.class)
        .block();
  }

  public record ManyUsersResponseDTO(List<UserInfoEntity> users, List<Long> remaining) {}
}
