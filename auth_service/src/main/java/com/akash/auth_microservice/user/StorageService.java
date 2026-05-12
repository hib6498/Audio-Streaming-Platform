package com.akash.auth_microservice.user;

import com.akash.auth_microservice.utils.FileUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
  private final Path storagePath;

  public StorageService(@Value("${STORAGE_LOCATION}") String storageLoc) {
    storagePath = Paths.get(storageLoc);
  }

  public String saveProfilePic(MultipartFile file, String id) throws FileAlreadyExistsException {
    String ext = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
    Path path;
    try {
      path = Files.createFile(storagePath.resolve(id + '.' + ext));
      file.transferTo(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return path.toString();
  }

  public Resource loadAsResource(String fileLoc) {
    try {
      Path file = Paths.get(fileLoc);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read file: " + fileLoc);
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not read file: " + fileLoc, e);
    }
  }
}
