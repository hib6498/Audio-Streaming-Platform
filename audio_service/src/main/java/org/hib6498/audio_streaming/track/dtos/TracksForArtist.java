package org.hib6498.audio_streaming.track.dtos;

import org.hib6498.audio_streaming.track.TrackEntity;

public record TracksForArtist(Long trackId, TrackInfo trackInfo) {
  public static TracksForArtist from(TrackEntity track, String url) {
    return new TracksForArtist(track.getTrackId(), TrackInfo.from(track));
  }
}
