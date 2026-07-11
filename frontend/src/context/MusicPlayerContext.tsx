import { createContext } from "react";
import { type TrackInfo } from "@/types/track.types";

interface MusicPlayerContextType {
  track: TrackInfo | null;
  playing: boolean;
  currentTime: number;
  duration: number;
  volume: number;
  play: (track: TrackInfo, trackId: number) => void;
  pause: () => void;
  toggle: () => void;
  seek: (seconds: number) => void;
  setVolume: (vol: number) => void;
  loadTrack: (track: TrackInfo, streamUrl: string) => void;
}

const MusicPlayerContext = createContext<MusicPlayerContextType | null>(null);

export default MusicPlayerContext;