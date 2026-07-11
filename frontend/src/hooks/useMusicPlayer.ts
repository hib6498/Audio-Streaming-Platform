import { useContext } from "react";
import MusicPlayerContext from "@/context/MusicPlayerContext";

const useMusicPlayer = () => {
  const ctx = useContext(MusicPlayerContext);
  if (!ctx)
    throw new Error("useMusicPlayer must be used within a MusicPlayerProvider");
  return ctx;
};

export default useMusicPlayer;
