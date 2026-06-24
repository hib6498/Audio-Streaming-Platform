package org.hib6498.audio_streaming.playlist;

import java.util.List;
import java.util.Objects;
import org.hib6498.audio_streaming.exceptions.NotFoundException;
import org.hib6498.audio_streaming.exceptions.UnauthorizedAccessException;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistInfoResponse;
import org.hib6498.audio_streaming.playlist.dtos.PlaylistRequest;
import org.hib6498.audio_streaming.track.TrackEntity;
import org.hib6498.audio_streaming.user.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaylistService {
  @Autowired private PlaylistRepository playlistRepository;
  @Autowired private PlaylistTrackRepository playlistTrackRepository;

  public List<PlaylistInfoResponse> getAllPlaylists() {
    List<PlaylistEntity> playlists = playlistRepository.findAll();
    return playlists.stream().map(PlaylistInfoResponse::from).toList();
  }

  public PlaylistEntity getPlaylist(Long playlistId) {
    return playlistRepository
        .findById(playlistId)
        .orElseThrow(() -> new NotFoundException("No playlist found"));
  }

  public PlaylistEntity getFullPlaylist(Long playlistId) {
    return playlistRepository
        .findByIdWithTracks(playlistId)
        .orElseThrow(() -> new NotFoundException("No playlist found."));
  }

  public PlaylistEntity createNewPlaylist(PlaylistRequest request, UserInfoEntity user) {
    PlaylistEntity newPlaylist = new PlaylistEntity().setTitle(request.title()).setUserInfo(user);
    return playlistRepository.save(newPlaylist);
  }

  @Transactional
  public PlaylistEntity addTrackToPlaylist(TrackEntity track, Long playlistId, Long userId) {
    PlaylistEntity playlist = getFullPlaylist(playlistId);

    if (!playlist.getUserInfo().getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You do not own this playlist");
    }

    if (playlist.getTracks().stream()
        .anyMatch(tracks -> Objects.equals(tracks.getKey().getTrackId(), track.getTrackId()))) {
      return playlist;
    }

    playlist
        .getTracks()
        .add(
            new PlaylistTrackEntity()
                .setKey(new PlaylistTrackKey())
                .setPlaylist(playlist)
                .setTrack(track));
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

  public void deleteTrackInstances(TrackEntity track) {
    playlistTrackRepository.deleteAllByTrack(track);
  }

  public List<PlaylistInfoResponse> getAllPlaylistsOfUser(Long userId) {
    List<PlaylistEntity> playlistEntities = playlistRepository.findAllByUserInfo_UserId(userId);
    return playlistEntities.stream().map(PlaylistInfoResponse::from).toList();
  }
}
