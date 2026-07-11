import { createContext } from "react";
import { type PlaylistContextType } from "@/types/playlist.types";

const PlaylistContext = createContext<PlaylistContextType | null>(null);

export default PlaylistContext;
