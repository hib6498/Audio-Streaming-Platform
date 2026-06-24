package org.hib6498.audio_streaming.track.dtos;

import org.hib6498.audio_streaming.track.TrackEntity;

public record TracksForArtistDTO(Long trackId, TrackInfo trackInfo) {
  public static TracksForArtistDTO from(TrackEntity track, String url) {
    return new TracksForArtistDTO(track.getTrackId(), TrackInfo.from(track, url));
  }
}
