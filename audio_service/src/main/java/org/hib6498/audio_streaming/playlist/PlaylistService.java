package org.hib6498.audio_streaming.playlist;

import org.hib6498.audio_streaming.entities.PlaylistEntity;
import org.hib6498.audio_streaming.entities.TrackEntity;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.exceptions.UnauthorizedAccessException;
import org.hib6498.audio_streaming.respositories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {
  @Autowired private PlaylistRepository playlistRepository;

  public PlaylistEntity createNewPlaylist(PlaylistRequest request) {
    PlaylistEntity newPlaylist = new PlaylistEntity().setTitle(request.title());
    return playlistRepository.save(newPlaylist);
  }

  public PlaylistEntity addTrackToPlaylist(TrackEntity track, Long playlistId, Long userId) {
    PlaylistEntity playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(() -> new NotFoundException("No playlist found."));

    if (!playlist.getUserInfo().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You do not own this playlist");
    }

    playlist.getTracks().add(track);
    playlistRepository.save(playlist);
    return playlist;
  }
}
