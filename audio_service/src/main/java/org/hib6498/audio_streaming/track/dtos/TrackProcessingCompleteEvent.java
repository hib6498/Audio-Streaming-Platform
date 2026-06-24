package org.hib6498.audio_streaming.track.dtos;

public record TrackProcessingCompleteEvent(Long trackId, String s3Key, String status) {}
