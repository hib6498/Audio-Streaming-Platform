import PlayIcon from "@/components/PlayIcon";
import PauseIcon from "@/components/PauseIcon";
import VolumeIcon from "@/components/VolumeIcon";
import useMusicPlayer from "@/hooks/useMusicPlayer";
import expandIcon from "@/assets/fullscreen.svg";
import minimizeIcon from "@/assets/minimize.svg";
import { cn } from "@sglara/cn";
import { useState } from "react";

const fmt = (s: number) => {
  const m = Math.floor(s / 60);
  const sec = Math.floor(s % 60);
  return `${m}:${String(sec).padStart(2, "0")}`;
};

export const MusicPlayer = () => {
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);
  const {
    track,
    playing,
    toggle,
    seek,
    currentTime,
    duration,
    volume,
    setVolume,
  } = useMusicPlayer();

  const trackInfo = track ?? null;

  const artistLabel = trackInfo
    ? trackInfo.owns.displayName +
      (trackInfo.features.length
        ? " feat. " + trackInfo.features.map((f) => f.displayName).join(", ")
        : "")
    : null;

  const progress = duration ? (currentTime / duration) * 100 : 0;

  return (
    <div
      className={cn(
        "fixed flex flex-col w-full bottom-0",
        isFullscreen ? "h-full z-10" : "max-h-18",
      )}
    >
      {isFullscreen && (
        <div className="relative flex-1 justify-center items-center flex">
          <div className="bg-black absolute -z-1 h-full w-full"></div>
          <div className="z-5 w-lg size-fit rounded-md overflow-hidden border">
            {trackInfo?.artworkUrl ? (
              <img
                src={trackInfo.artworkUrl}
                className="object-cover"
                alt={trackInfo.title}
              />
            ) : (
              <div className="size-64 bg-gray-800 flex items-center justify-center text-gray-600">
                No track selected
              </div>
            )}
          </div>
        </div>
      )}
      <div className="h-18 shrink-0 border-t bg-black px-5">
        <div className="max-w-6xl h-full gap-8 flex items-center justify-between mx-auto">
          <div className="flex gap-4 items-center">
            <div className="size-11 shrink-0 rounded-md overflow-hidden bg-white/20">
              {trackInfo?.artworkUrl ? (
                <img
                  src={trackInfo.artworkUrl}
                  className="size-full object-cover"
                  alt={trackInfo.title}
                />
              ) : (
                <div className="size-full flex items-center justify-center text-gray-600"></div>
              )}
            </div>

            <div className="max-w-36 w-fit min-w-0">
              {trackInfo ? (
                <>
                  <p className="text-[0.95em] font-medium truncate">
                    {trackInfo.title}
                  </p>
                  <p className="text-xs text-gray-400 truncate">
                    {artistLabel}
                  </p>
                </>
              ) : (
                <>
                  <div className="h-3.5 w-24 bg-white/20 rounded mb-1.5" />
                  <div className="h-3 w-16 bg-white/10 rounded" />
                </>
              )}
            </div>
          </div>

          <div className="flex gap-3 w-full max-w-lg">
            <button
              onClick={toggle}
              disabled={!trackInfo}
              className="size-10 rounded-full border flex items-center justify-center shrink-0 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
            >
              {playing ? <PauseIcon /> : <PlayIcon />}
            </button>

            <div className="flex flex-1 items-center gap-2 min-w-0">
              <span className="text-sm text-gray-400 w-8 text-right">
                {fmt(currentTime)}
              </span>
              <input
                type="range"
                min={0}
                max={100}
                step={0.1}
                value={progress}
                disabled={!trackInfo}
                onChange={(e) =>
                  seek((Number(e.target.value) / 100) * duration)
                }
                className="flex-1 min-w-0 h-1 disabled:opacity-30 disabled:cursor-not-allowed"
              />
              <span className="text-sm text-gray-400 w-8">{fmt(duration)}</span>
            </div>
          </div>

          <div className="flex w-full max-w-50 lg:max-w-50 gap-3 shrink-0">
            <div className="flex items-center gap-2">
              <VolumeIcon />
              <input
                type="range"
                min={0}
                max={1}
                step={0.01}
                value={volume}
                disabled={!trackInfo}
                onChange={(e) => setVolume(Number(e.target.value))}
                className="w-full h-1 disabled:opacity-30 disabled:cursor-not-allowed"
              />
            </div>
            {isFullscreen ? (
              <img
                onClick={() => setIsFullscreen(false)}
                className="size-10 cursor-pointer rounded-full p-0.5 invert-100 hover:bg-gray-600/20"
                src={minimizeIcon}
                alt="Minimize music player"
              />
            ) : (
              <img
                onClick={() => setIsFullscreen(true)}
                className="size-10 cursor-pointer rounded-full p-2 invert-100 hover:bg-gray-600/20"
                src={expandIcon}
                alt="Fullscreen music player"
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
