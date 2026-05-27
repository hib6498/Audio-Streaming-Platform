package org.hib6498.audio_streaming.playlist;

import java.util.List;

import org.hib6498.audio_streaming.entities.*;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.exceptions.UnauthorizedAccessException;
import org.hib6498.audio_streaming.respositories.PlaylistRepository;
import org.hib6498.audio_streaming.respositories.PlaylistTrackRepository;
import org.hib6498.audio_streaming.shared.UserIdDisplayNameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaylistService {
  @Autowired private PlaylistRepository playlistRepository;
  @Autowired private PlaylistTrackRepository playlistTrackRepository;

  public List<PlaylistInfoResponseDTO> getAllPlaylists() {
    List<PlaylistEntity> playlists = playlistRepository.findAll();
      return playlists.stream()
          .map(
              playlist ->
                  new PlaylistInfoResponseDTO(
                      playlist.getPlaylistId(),
                      playlist.getTitle(),
                      new UserIdDisplayNameDTO(
                          playlist.getUserInfo().getUserId(),
                          playlist.getUserInfo().getDisplayName()),
                      playlist.getCoverArt())).toList();
  }

  public PlaylistEntity getPlaylist(Long playlistId) {
    return playlistRepository
        .findById(playlistId)
        .orElseThrow(() -> new NotFoundException("No playlist found"));
  }

  public PlaylistEntity createNewPlaylist(PlaylistRequest request, UserInfoEntity user) {
    PlaylistEntity newPlaylist = new PlaylistEntity().setTitle(request.title()).setUserInfo(user);
    return playlistRepository.save(newPlaylist);
  }

  @Transactional
  public PlaylistEntity addTrackToPlaylist(TrackEntity track, Long playlistId, Long userId) {
    PlaylistEntity playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(() -> new NotFoundException("No playlist found."));

    if (!playlist.getUserInfo().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You do not own this playlist");
    }

    PlaylistTrackEntity newEntry =
        new PlaylistTrackEntity()
            .setPlaylist(playlist)
            .setTrack(track)
            .setKey(
                new PlaylistTrackKey().setPlaylistId(playlistId).setTrackId(track.getTrackId()));
    playlistTrackRepository.save(newEntry);
    return playlist;
  }

  public PlaylistEntity removeTrackFromPlaylist(TrackEntity track, Long playlistId, Long userId) {
    PlaylistEntity playlist =
        playlistRepository
            .findById(playlistId)
            .orElseThrow(() -> new NotFoundException("No playlist found."));

    if (!playlist.getUserInfo().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You do not own this playlist");
    }

    PlaylistTrackEntity deleteEntry =
        new PlaylistTrackEntity()
            .setPlaylist(playlist)
            .setTrack(track)
            .setKey(
                new PlaylistTrackKey().setPlaylistId(playlistId).setTrackId(track.getTrackId()));
    playlistTrackRepository.delete(deleteEntry);
    return playlist;
  }
}
