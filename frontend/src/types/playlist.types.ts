import type { TrackInfo, TrackUserInfo } from "./track.types";

interface PlaylistContextType {
  playlists: PlaylistInfo[];
  createNewPlaylist: (playlistTitle: string) => Promise<void>;
  addTrackToPlaylist: (
    playlistId: number,
    trackId: number,
  ) => Promise<PlaylistInfoWithTracks | null>;
  removeFromPlaylist: (playlistId: number, trackId: number) => Promise<void>;
}

interface PlaylistInfo {
  id: number;
  title: string;
  userInfo: TrackUserInfo;
  coverArt: string;
}

interface PlaylistInfoWithTracks extends PlaylistInfo {
  createdAt: EpochTimeStamp;
  tracks: TrackInfo[];
}

interface CreatePlaylistRequest {
  title: string;
}

export type {
  PlaylistInfo,
  PlaylistInfoWithTracks,
  CreatePlaylistRequest,
  PlaylistContextType,
};
