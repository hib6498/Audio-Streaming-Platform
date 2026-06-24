package org.hib6498.audio_streaming.track.dtos;

public record TrackPresignedUploadResponse(Long trackId, Long uploadId, String s3Url) {}
