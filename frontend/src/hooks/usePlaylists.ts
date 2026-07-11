import { useContext } from "react";
import { type PlaylistContextType } from "@/types/playlist.types";
import PlaylistContext from "@/context/PlaylistContext";

const usePlaylists = (): PlaylistContextType => {
  const context = useContext(PlaylistContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthContextProvider");
  }

  return context;
};

export default usePlaylists;
