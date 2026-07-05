package org.hib6498.audio_streaming.track.dtos;

import java.util.Optional;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public record UpdateTrackMetadataRequest(
    String trackName, MultipartFile coverArt, Optional<Set<Long>> featureIds) {}
