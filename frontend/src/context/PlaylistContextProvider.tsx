import { useEffect, useState } from "react";
import PlaylistContext from "./PlaylistContext";
import type {
  PlaylistInfo,
  PlaylistInfoWithTracks,
} from "@/types/playlist.types";
import {
  createPlaylist,
  getPlaylistsForUser,
  addToPlaylist,
  removeTrackFromPlaylist
} from "@/services/playlistService";
import useAuth from "@/hooks/useAuth";

const PlaylistContextProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { user } = useAuth();
  const [playlists, setPlaylists] = useState<PlaylistInfo[]>([]);

  useEffect(() => {
    if (user)
      getPlaylistsForUser(user?.userId).then((res) => setPlaylists(res));
  }, [user]);

  const removeFromPlaylist = async (playlistId: number, trackId: number): Promise<void> => {
    await removeTrackFromPlaylist(playlistId, trackId);
  }

  const createNewPlaylist = async (playlistTitle: string): Promise<void> => {
    const newPlaylist = await createPlaylist({ title: playlistTitle });
    setPlaylists((prev) => prev.concat([newPlaylist]));
  };

  const addTrackToPlaylist = async (
    playlistId: number,
    trackId: number,
  ): Promise<PlaylistInfoWithTracks | null> => {
    const playlist = playlists.find((playlist) => playlist.id === playlistId);
    if (!playlist) return null;

    return await addToPlaylist(playlistId, trackId);
  };

  return (
    <PlaylistContext.Provider
      value={{ playlists, createNewPlaylist, addTrackToPlaylist, removeFromPlaylist }}
    >
      {children}
    </PlaylistContext.Provider>
  );
};

export default PlaylistContextProvider;
