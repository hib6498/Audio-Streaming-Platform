import useMusicPlayer from "@/hooks/useMusicPlayer";
import { useQueryClient } from "@tanstack/react-query";
import { getPlaylist } from "@/services/playlistService";
import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import type { PlaylistInfoWithTracks } from "@/types/playlist.types";
import TrackCard from "@/components/TrackCard";
import useAuth from "@/hooks/useAuth";
import usePlaylists from "@/hooks/usePlaylists";

const Playlist: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const { play } = useMusicPlayer();
  const { removeFromPlaylist } = usePlaylists();

  const {
    data: playlist,
    isLoading: isPlaylistLoading,
    isError,
  } = useQuery<PlaylistInfoWithTracks>({
    queryKey: ["playlist", id],
    queryFn: ({ queryKey }) =>
      getPlaylist(Number.parseInt(queryKey[1] as string)),
  });

  const queryClient = useQueryClient();

  const removeFromPlaylistCallback = async (trackId: number) => {
    if (!id) return;
    await removeFromPlaylist(id as unknown as number, trackId);
    queryClient.invalidateQueries({ queryKey: ["playlist", id] });
  };

  return (
    <article className="max-w-6xl mx-auto px-4">
      <section className="relative flex items-center gap-12 px-5 py-8 overflow-hidden">
        <div
          className="absolute inset-0 z-0 bg-red-100"
          style={{
            backgroundImage: `url(${playlist?.coverArt})`,
            backgroundSize: "cover",
            backgroundPosition: "center",
          }}
        >
          <div className="w-full h-full backdrop-blur-xl scale-110"></div>
        </div>
        <div className="absolute inset-0 bg-black/70 z-0"></div>
        <div className="relative z-10 flex items-center gap-12">
          <div className="relative w-fit group">
            <img
              src={playlist?.coverArt}
              className="size-44 border-white/20 border rounded-sm"
            />
          </div>
          <div>
            <p className="px-3 bg-gray-950 mb-2">Playlist</p>
            <h1 className="bg-gray-950 text-2xl font-bold w-fit px-3">
              {playlist?.title}
            </h1>
            <h1 className="bg-gray-950 font-semibold opacity-85 px-3 w-fit">
              {playlist?.userInfo.displayName}
            </h1>
          </div>
        </div>
      </section>

      {playlist && !isPlaylistLoading && (
        <section className="my-4">
          {playlist.tracks.map((track) => (
            <TrackCard
              playTrack={() => play(track, track.trackId)}
              trackId={track.trackId}
              removeFromPlaylistCallback={
                playlist.userInfo.userId === user?.userId
                  ? () => removeFromPlaylistCallback(track.trackId)
                  : undefined
              }
              trackInfo={track}
            />
          ))}
        </section>
      )}
      {isError && <p>Error occured</p>}
    </article>
  );
};

export default Playlist;
