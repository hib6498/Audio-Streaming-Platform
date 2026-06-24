package org.hib6498.audio_streaming.store;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.hib6498.audio_streaming.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
  private final Path trackStoragePath;
  private final Path artworkStoragePath;

  public StorageService(
      @Value("${TRACK_STORAGE_LOCATION}") String trackStorageLoc,
      @Value("${ARTWORK_STORAGE_LOCATION}") String artworkStorageLoc) {
    trackStoragePath = Paths.get(trackStorageLoc);
    artworkStoragePath = Paths.get(artworkStorageLoc);
  }

  public String saveTrack(MultipartFile file) {
    return transferFile(file, trackStoragePath);
  }

  public String saveArtwork(MultipartFile file) {
    return transferFile(file, artworkStoragePath);
  }

  private String transferFile(MultipartFile file, Path storageLoc) {
    Path path;
    while (true) {
      String id = UUID.randomUUID().toString();

      String ext = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
      try {
        path = Files.createFile(storageLoc.resolve(id + '.' + ext));
        file.transferTo(path);
      } catch (FileAlreadyExistsException e) {
        continue;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      break;
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
