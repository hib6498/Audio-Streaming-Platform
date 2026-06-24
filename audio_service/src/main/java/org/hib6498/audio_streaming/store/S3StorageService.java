package org.hib6498.audio_streaming.store;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Duration;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3StorageService {
  @Autowired private S3Client s3Client;
  //  @Autowired private S3AsyncClient s3AsyncClient;
  @Autowired private S3Presigner presigner;
  @Autowired private StorageStatusRepository storageStatusRepository;

  @Value("${s3.track-bucket}")
  private String trackBucket;

  @Value("${s3.assets-bucket}")
  private String assetsBucket;

  public PresignedPutObjectRequest generatePresignedPutRequest(PutObjectRequest request) {
    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .putObjectRequest(request)
            .signatureDuration(Duration.ofMinutes(20))
            .build();

    return presigner.presignPutObject(presignRequest);
  }

  public PresignedGetObjectRequest generatePresignedGetRequest(GetObjectRequest request) {
    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .getObjectRequest(request)
            .signatureDuration(Duration.ofMinutes(20))
            .build();

    return presigner.presignGetObject(presignRequest);
  }

  public URL generatePresignedGetUrlForTrack(TrackEntity track) {
    GetObjectRequest request =
        GetObjectRequest.builder().bucket(trackBucket).key(track.getStore().getS3Key()).build();
    return generatePresignedGetRequest(request).url();
  }

  @Transactional
  public Pair<StorageStatusEntity, URL> generatePresignedUrlForTrackUpload(TrackEntity track) {
    StorageStatusEntity statusEntity =
        new StorageStatusEntity()
            .setStatus(UploadStatus.PENDING)
            .setTrack(track)
            .setS3Key(track.getTrackId().toString() + "/" + track.getTitle());
    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(trackBucket)
            .key(track.getTrackId() + "/" + track.getTitle())
            .build();

    PresignedPutObjectRequest presignedPutObjectRequest = generatePresignedPutRequest(request);
    statusEntity.setExpiresAt(Timestamp.from(presignedPutObjectRequest.expiration()));
    StorageStatusEntity entry = storageStatusRepository.save(statusEntity);
    track.setStore(entry);
    return Pair.of(entry, presignedPutObjectRequest.url());
  }

  public StorageStatusEntity presignedPutRequestComplete(Long requestId) {
    StorageStatusEntity statusEntity =
        storageStatusRepository
            .findById(requestId)
            .orElseThrow(() -> new NotFoundException("No url entry found"));
    statusEntity.setStatus(UploadStatus.COMPLETED);
    return storageStatusRepository.save(statusEntity);
  }

  public void deleteObject(String s3Key) {
    s3Client.deleteObject(req -> req.bucket(trackBucket).key(s3Key));
  }

  // cant use it as garage doesnt support public buckets
  // this will work, but will need to serve assets with presigned url, which is dumb
  //  public PutObjectResponse uploadAsset(MultipartFile file, String key) {
  //    try {
  //      return s3AsyncClient
  //          .putObject(
  //              req ->
  //                  req.bucket(assetsBucket)
  //                      .key(key)
  //                      .contentType(file.getContentType())
  //                      .contentLength(file.getSize()),
  //              AsyncRequestBody.fromInputStream(
  //                  file.getInputStream(), file.getSize(), Executors.newSingleThreadExecutor()))
  //          .join();
  //    } catch (IOException e) {
  //      throw new StorageException("Failed to upload file" + file.getName());
  //    }
  //  }
}
