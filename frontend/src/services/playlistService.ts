import { instance as audioAxiosInstance } from "@/config/trackAxiosInstance";
import {
  type CreatePlaylistRequest,
  type PlaylistInfo,
  type PlaylistInfoWithTracks,
} from "@/types/playlist.types";

const getAllPlaylists = async () => {
  const { data } =
    await audioAxiosInstance.get<PlaylistInfo[]>("/playlist/all");
  return data;
};

const getPlaylist = async (playlistId: number) => {
  const { data } = await audioAxiosInstance.get<PlaylistInfoWithTracks>(
    `/playlist/${playlistId}`,
  );
  return data;
};

const getPlaylistsForUser = async (userId: number): Promise<PlaylistInfo[]> => {
  const { data } = await audioAxiosInstance.get<PlaylistInfo[]>(
    `/user/${userId}/playlists`,
  );
  return data;
};

const createPlaylist = async (
  request: CreatePlaylistRequest,
): Promise<PlaylistInfo> => {
  const { data } = await audioAxiosInstance.post<PlaylistInfo>(
    "/playlist/new",
    request,
  );
  return data;
};

const addToPlaylist = async (playlistId: number, trackId: number) => {
  const { data } = await audioAxiosInstance.put<PlaylistInfoWithTracks>(
    `/playlist/${playlistId}/tracks/${trackId}`,
  );
  return data;
};

const removeTrackFromPlaylist = async (
  playlistId: number,
  trackId: number,
): Promise<PlaylistInfoWithTracks> => {
  const { data } = await audioAxiosInstance.delete<PlaylistInfoWithTracks>(
    `playlist/${playlistId}/tracks/${trackId}`,
  );
  return data;
};

export {
  getAllPlaylists,
  getPlaylist,
  createPlaylist,
  getPlaylistsForUser,
  addToPlaylist,
  removeTrackFromPlaylist
};
