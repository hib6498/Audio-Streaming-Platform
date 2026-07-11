import type { TrackInfo } from "@/types/track.types";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AddToPlaylistButton from "./AddToPlaylistButton";
import DropdownMenu from "./DropdownMenu";
import usePlaylists from "@/hooks/usePlaylists";
import { useMutation } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import type { PlaylistInfoWithTracks } from "@/types/playlist.types";
import Input from "./Input";
import tickSvg from "@/assets/tick.svg";
import { cn } from "@sglara/cn";

interface UserRedirectLinkProps {
  userId: number;
  displayName: string;
}

const UserRedirectLink: React.FC<UserRedirectLinkProps> = ({
  userId,
  displayName,
}) => {
  const navigate = useNavigate();
  return (
    <span
      onClick={() => {
        navigate(`/user/${userId}`);
      }}
      className="text-white opacity-60 hover:underline cursor-pointer disabled:opacity-30"
    >
      {displayName}
    </span>
  );
};

interface PlaylistOptionProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  name: string;
  prefix?: string;
  showHover?: boolean;
  children?: React.ReactNode;
}

const PlaylistOption: React.FC<PlaylistOptionProps> = ({
  name,
  className,
  showHover = true,
  prefix = "",
  children,
  ...props
}) => {
  return (
    <button
      type="button"
      className={cn(
        "duration-200 rounded-sm cursor-pointer w-full py-2.5 px-2 flex items-center gap-1",
        showHover && "hover:bg-white/15",
        className,
      )}
      {...props}
    >
      {children ?? (
        <>
          <span className="font-light text-xl h-fit leading-0">{prefix}</span>
          <span>{name}</span>
        </>
      )}
    </button>
  );
};

interface TrackCardProps {
  trackId: number;
  trackInfo: TrackInfo;
  removeFromPlaylistCallback?: () => Promise<void>;
  playTrack: () => void;
}

const TrackCard: React.FC<TrackCardProps> = ({
  trackId,
  trackInfo,
  removeFromPlaylistCallback,
  playTrack,
}) => {
  const [showDropdown, setShowDropdown] = useState<boolean>(false);
  const [rightOffset, setRightOffset] = useState<number>(0);
  const { playlists, addTrackToPlaylist, createNewPlaylist } = usePlaylists();
  const [isCreatingPlaylist, setIsCreatingPlaylist] = useState<boolean>(false);

  const {
    mutateAsync: addTrackToPlaylistCallback,
    isPending: isAddTrackLoading,
  } = useMutation<
    PlaylistInfoWithTracks | null,
    AxiosError,
    { trackId: number; playlistId: number }
  >({
    mutationFn: ({ trackId, playlistId }) =>
      addTrackToPlaylist(playlistId, trackId),
  });

  const {
    mutateAsync: createNewPlaylistCallback,
    isPending: isCreatePlaylistPending,
  } = useMutation<void, AxiosError, { title: string }>({
    mutationFn: ({ title }) => createNewPlaylist(title),
  });

  const createNewPlaylistEvent = async (formData: FormData): Promise<void> => {
    const playlist = String(formData.get("title") ?? "");

    try {
      if (!playlist) return;
      await createNewPlaylistCallback({ title: playlist });
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (_error) {
      //
    } finally {
      setIsCreatingPlaylist(false);
    }
  };

  return (
    <div
      key={trackId}
      className="hover:bg-gray-100/5 group flex p-3.5 gap-6 rounded-lg"
    >
      <div className="size-20 rounded-sm items-center justify-center flex">
        <img src={trackInfo.artworkUrl} alt="Cover Art" />
      </div>
      <div>
        <h1
          className="font-bold text-xl cursor-pointer hover:text-underline"
          onClick={playTrack}
        >
          {trackInfo.title}
        </h1>
        {trackInfo.features.map((user, idx) => (
          <>
            {idx !== 0 && ", "}
            <UserRedirectLink
              userId={user.userId}
              displayName={user.displayName}
            />
          </>
        ))}
        <p className="opacity-60">
          {new Date(trackInfo.createdAt).getFullYear()}
        </p>
      </div>
      <div className="h-full flex self-center ml-auto relative">
        <AddToPlaylistButton
          setRightOffset={setRightOffset}
          onClick={() => setShowDropdown((prev) => !prev)}
        />
        {removeFromPlaylistCallback ? (
          <button
            type="button"
            onClick={() => removeFromPlaylistCallback()}
            className="rounded-full size-8 opacity-0 group-hover:opacity-40 cursor-pointer duration-200 hover:opacity-80 hover:bg-gray-200/10 text-xl font-bold"
          >
            -
          </button>
        ) : undefined}
      </div>
      {showDropdown && (
        <DropdownMenu
          className="max-w-60 mt-14 w-full flex flex-col items-center p-4 text-white/80"
          style={{ right: rightOffset }}
          showDropdown={showDropdown}
          closeDropdown={() => setShowDropdown(false)}
        >
          <h1 className="opacity-60">Add to playlist</h1>
          <div className="flex duration-200 w-full">
            {!isCreatingPlaylist ? (
              <PlaylistOption
                onClick={
                  isCreatingPlaylist
                    ? undefined
                    : () => setIsCreatingPlaylist(true)
                }
                showHover={!isCreatingPlaylist}
                name="New Playlist"
                prefix="+"
              />
            ) : (
              <form
                action={createNewPlaylistEvent}
                className="flex items-center gap-1"
              >
                <Input name="title" placeholder="Title" />
                <button
                  disabled={isCreatePlaylistPending}
                  className="hover:bg-white/20 rounded-sm cursor-pointer"
                >
                  <img
                    src={tickSvg}
                    className="invert size-10 outline-0 border-white"
                  />
                </button>
              </form>
            )}
          </div>
          <hr className="w-full opacity-30" />
          <div className="flex flex-col w-full">
            {playlists.length === 0 && <p>No playlists</p>}
            {playlists?.map((playlist) => (
              <PlaylistOption
                onClick={() => {
                  addTrackToPlaylistCallback({
                    playlistId: playlist.id,
                    trackId,
                  });
                  setShowDropdown(false);
                }}
                name={playlist.title}
                disabled={isAddTrackLoading}
              />
            ))}
          </div>
        </DropdownMenu>
      )}
    </div>
  );
};

export default TrackCard;
