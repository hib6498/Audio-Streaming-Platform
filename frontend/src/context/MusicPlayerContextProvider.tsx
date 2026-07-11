import { useRef, useState, useEffect, type ReactNode } from "react";
import { type TrackInfo } from "@/types/track.types";
import MusicPlayerContext from "./MusicPlayerContext";

import { getTrackStreamUrl } from "@/services/trackService";

export const MusicPlayerProvider = ({ children }: { children: ReactNode }) => {
  const audioRef = useRef<HTMLAudioElement>(new Audio());
  const [track, setTrack] = useState<TrackInfo | null>(null);
  const [playing, setPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolumeState] = useState(1);

  useEffect(() => {
    const a = audioRef.current;

    const onPlay = () => setPlaying(true);
    const onPause = () => setPlaying(false);
    const onMeta = () => setDuration(a.duration);
    const onTime = () => setCurrentTime(a.currentTime);
    const onEnd = () => {
      setPlaying(false);
      setCurrentTime(0);
    };

    a.addEventListener("play", onPlay);
    a.addEventListener("pause", onPause);
    a.addEventListener("loadedmetadata", onMeta);
    a.addEventListener("timeupdate", onTime);
    a.addEventListener("ended", onEnd);

    return () => {
      a.removeEventListener("play", onPlay);
      a.removeEventListener("pause", onPause);
      a.removeEventListener("loadedmetadata", onMeta);
      a.removeEventListener("timeupdate", onTime);
      a.removeEventListener("ended", onEnd);
      a.pause();
    };
  }, []);

  const loadTrack = (newTrack: TrackInfo, streamUrl: string) => {
    const a = audioRef.current;
    a.pause();
    a.src = streamUrl;
    a.load();
    setTrack(newTrack);
    setCurrentTime(0);
    setDuration(0);
    setPlaying(false);
  };

  const play = async (track: TrackInfo, trackId: number) => {
    if (track && trackId) {
      const url = await getTrackStreamUrl(trackId);
      loadTrack(track, url);
    }
    audioRef.current.play();
  };

  const pause = () => audioRef.current.pause();

  const toggle = () => (playing ? pause() : audioRef.current.play());

  const seek = (seconds: number) => {
    audioRef.current.currentTime = seconds;
    setCurrentTime(seconds);
  };

  const setVolume = (vol: number) => {
    audioRef.current.volume = vol;
    setVolumeState(vol);
  };

  return (
    <MusicPlayerContext.Provider
      value={{
        track,
        playing,
        currentTime,
        duration,
        volume,
        play,
        pause,
        toggle,
        seek,
        setVolume,
        loadTrack,
      }}
    >
      {children}
    </MusicPlayerContext.Provider>
  );
};
