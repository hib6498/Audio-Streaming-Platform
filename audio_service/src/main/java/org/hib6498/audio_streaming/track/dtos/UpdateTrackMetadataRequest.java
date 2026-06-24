package org.hib6498.audio_streaming.track;

import java.util.Optional;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public record TrackUploadRequestDTO(
    String trackName,
    MultipartFile coverArt,
    Optional<Long> albumId,
    Optional<Set<Long>> featureIds) {}
