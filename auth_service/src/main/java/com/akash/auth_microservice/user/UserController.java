package com.akash.auth_microservice.user;

import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired private UserService userService;
  @Autowired private StorageService storageService;

  @GetMapping("/me")
  public ResponseEntity<UserDTO> getUserForToken(@AuthenticationPrincipal User user) {
    UserDTO response =
        new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getDisplayName());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserInfoDTO> getUser(@PathVariable Long userId) {
    User user = userService.loadUserById(userId);

    if (Objects.isNull(user)) return ResponseEntity.notFound().build();

    UserInfoDTO response =
        new UserInfoDTO(
            user.getUserId(),
            user.getDisplayName(),
            user.getUsername(),
            user.getProfile().getProfilePic());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}/profile-pic")
  public ResponseEntity<Resource> getUserProfilePic(@PathVariable Long userId) {
    Optional<Resource> pic = userService.getProfilePic(userId);

    return pic.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/update/description")
  public ResponseEntity<DescriptionDTO> updateDescription(
      @RequestBody DescriptionDTO desc, @AuthenticationPrincipal User user) {
    userService.updateDescription(desc.desc(), user);
    return ResponseEntity.ok(desc);
  }

  @PostMapping(value = "/update/profile-pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Profile updateProfilePic(
      @AuthenticationPrincipal User user, @RequestPart MultipartFile file) {

    String saveLoc;
    while (true) {
      try {
        UUID uuid = UUID.randomUUID();
        saveLoc = storageService.saveProfilePic(file, uuid.toString());
      } catch (FileAlreadyExistsException e) {
        continue;
      }

      break;
    }

    return userService.updateProfilePic(saveLoc, user);
  }

  public record UserDTO(Long userId, String username, String email, String displayName) {}

  public record DescriptionDTO(String desc) {}
}
