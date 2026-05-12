package com.akash.auth_microservice.user;

import com.akash.auth_microservice.exceptions.UserIdNotFound;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  @Autowired private UserRepository userRepository;
  @Autowired private ProfileRepository profileRepository;
  @Autowired private UserEventProducer producer;
  @Autowired private StorageService storageService;

  public Profile initializeProfile(User user) {
    Profile newUser = new Profile().setUser(user);
    return profileRepository.save(newUser);
  }

  public Profile updateProfilePic(String saveLoc, User user) {
    Optional<Profile> query = profileRepository.findById(user.getUserId());
    Profile profile = query.orElse(new Profile().setUser(user));

    profile.setProfilePic(saveLoc);
    Profile savedProfile = profileRepository.save(profile);
    sendProfileUpdate(savedProfile);

    return savedProfile;
  }

  public void updateDescription(String desc, User user) {
    Profile profile = user.getProfile();
    profile.setDescription(desc);
    profileRepository.save(profile);
  }

  public Optional<Resource> getProfilePic(Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) return Optional.empty();

    Optional<Profile> profile = profileRepository.findById(user.get().getUserId());
    if (profile.isEmpty() || profile.get().getProfilePic().isBlank()) return Optional.empty();

    return Optional.of(storageService.loadAsResource(profile.get().getProfilePic()));
  }

  public User loadUserById(Long id) throws UserIdNotFound {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new UserIdNotFound("Could not find user with id = " + id));
  }

  private void sendProfileUpdate(Profile profile) {
    User user = profile.getUser();
    UserInfoDTO message =
        new UserInfoDTO(
            user.getUserId(), user.getDisplayName(), user.getUsername(), profile.getProfilePic());
    producer.sendUserInfo(message);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsernameOrEmail(username, username)
        .orElseThrow(
            () ->
                new UsernameNotFoundException(
                    "Could not find user with username/email = " + username));
  }

  public UserLookupResult loadUsersByIds(List<Long> ids) {
    Set<Long> remaining = new HashSet<>(ids);

    List<User> result = userRepository.findAllById(ids);

    for (User user : result) {
      remaining.remove(user.getUserId());
    }

    return new UserLookupResult(result, new ArrayList<>(remaining));
  }
}
