import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useRef, useState } from "react";
import type { User } from "@/types/auth.type";
import Avatar from "@/components/Avatar";
import TrackCard from "@/components/TrackCard";
import { type TracksForArtist } from "@/types/track.types";
import { getTracksForArtists } from "@/services/trackService";
import { getArtist } from "@/services/artistService";
import useMusicPlayer from "@/hooks/useMusicPlayer";
import useAuth from "@/hooks/useAuth";
import Layover from "@/components/Layover";
import PhotoPreview from "./PhotoPreview";
import { isValidImageSize } from "@/utils/imageUtils";
import { type SelectedFile } from "./SelectedFile.type";
import { getPlaylistsForUser } from "@/services/playlistService";
import type { PlaylistInfo } from "@/types/playlist.types";
import AlbumCard from "@/components/AlbumCard";

const MIN_DIMENSION = 200;

const ArtistPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { play } = useMusicPlayer();
  const { user: loggedUser } = useAuth();
  const isOwner = Number.parseInt(id ?? "-1") === loggedUser?.userId;

  const [showLayover, setShowLayover] = useState(false);
  const [imageSrc, setImageSrc] = useState<SelectedFile | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const { data: user, isLoading: isUserLoading } = useQuery<User>({
    queryKey: ["artist", id],
    queryFn: ({ queryKey }) =>
      getArtist(Number.parseInt(queryKey[1] as string)),
  });

  const {
    data: tracks,
    isLoading: isTracksLoading,
    isError: isTracksError,
  } = useQuery<TracksForArtist[]>({
    queryKey: ["tracks-for-artist", id],
    queryFn: ({ queryKey }) =>
      getTracksForArtists(queryKey[1] as unknown as number),
  });

  const {
    data: playlists,
    isLoading: isPlaylistsLoading,
    isError: isPlaylistsError,
  } = useQuery<PlaylistInfo[]>({
    queryKey: ["playlists-of-artist", id],
    queryFn: ({ queryKey }) =>
      getPlaylistsForUser(queryKey[1] as unknown as number),
  });

  const selectImage: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.addEventListener("load", () => {
      const imageElement = new Image();
      const imageUrl = reader.result?.toString() || "";
      imageElement.src = imageUrl;

      imageElement.addEventListener("load", (e: Event) => {
        const { naturalWidth, naturalHeight } =
          e.currentTarget as HTMLImageElement;
        if (isValidImageSize(naturalWidth, naturalHeight, MIN_DIMENSION)) {
          console.log("insufficient image size");
          setImageSrc(null);
        } else {
          setImageSrc({ src: imageUrl, name: file.name });
        }
      });
    });
    reader.readAsDataURL(file);
  };

  if (isUserLoading || !user) return <p>loading...</p>;

  return (
    <div key={id} className="max-w-6xl mx-auto px-4">
      {isOwner && showLayover && imageSrc && (
        <Layover
          closeCallback={() => {
            setShowLayover(false);
            setImageSrc(null);
          }}
        >
          <div className="py-8 px-12 rounded-xl bg-black z-2 mx-auto">
            <PhotoPreview
              image={imageSrc}
              onUpdate={() => setShowLayover(false)}
            />
          </div>
        </Layover>
      )}

      <section>
        <div className="relative flex items-center gap-12 px-5 py-8 overflow-hidden">
          <div
            className="absolute inset-0 z-0"
            style={{
              backgroundImage: `url(${user.profilePic})`,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
          >
            <div className="w-full h-full backdrop-blur-xl scale-110"></div>
          </div>
          <div className="absolute inset-0 bg-black/70 z-0"></div>
          <div className="relative z-10 flex items-center gap-12">
            <div className="relative w-fit group">
              <Avatar withHover={isOwner} user={user} size="large" />
              {isOwner && (
                <div className="hidden group-hover:flex absolute inset-0 overflow-clip justify-center">
                  <div className="absolute opacity-40 z-1 bg-red-50 size-full rounded-full"></div>
                  <input
                    type="file"
                    accept=".jpg, .jpeg, .png"
                    name="profilePic"
                    className="size-px col-fit"
                    onChange={selectImage}
                    ref={inputRef}
                  />
                  <button
                    onClick={() => {
                      inputRef.current?.click();
                      setShowLayover(true);
                    }}
                    className="bg-black z-2 h-fit mt-32 opacity-100 rounded-sm p-2 font-semibold hover:text-gray-500 cursor-pointer text-sm"
                  >
                    Update
                  </button>
                </div>
              )}
            </div>
            <div>
              <h1 className="bg-gray-950 text-2xl font-bold w-fit px-3">
                {user.username}
              </h1>
              <h1 className="bg-gray-950 font-semibold opacity-85 px-3 w-fit">
                {user.displayName}
              </h1>
            </div>
          </div>
        </div>
      </section>

      <section className="my-6 flex flex-col gap-2">
        <h1 className="text-2xl font-extrabold">Recent Tracks</h1>
        {tracks && !isTracksLoading && (
          <div className="flex flex-col w-full">
            {tracks.map((track) => (
              <TrackCard
                playTrack={() => play(track.trackInfo, track.trackId)}
                trackId={track.trackId}
                trackInfo={track.trackInfo}
              />
            ))}
          </div>
        )}
        {isTracksError && <p>Error occured</p>}
      </section>
      <section className="my-6 flex flex-col gap-2">
        <h1 className="text-2xl font-extrabold">Playlists</h1>
        {!isPlaylistsLoading && playlists?.length === 0 && <p className="opacity-50">No playlists found</p>}
        {playlists && !isPlaylistsLoading && (
          <div className="flex w-full">
            {playlists.map((playlist) => (
              <AlbumCard playlist={playlist} />
            ))}
          </div>
        )}
        {isPlaylistsError && <p>Error occured</p>}
      </section>
    </div>
  );
};

export default ArtistPage;
