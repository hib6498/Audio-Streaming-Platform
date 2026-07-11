import ArtistCard from "@/components/ArtistCard";
// import AlbumCard from "@/components/AlbumCard";
import { useQuery } from "@tanstack/react-query";
import { getArtists } from "@/services/artistService";
import { getAllTracks } from "@/services/trackService";
import type { User } from "@/types/auth.type";
import type { TracksForArtist } from "@/types/track.types";
import TrackCard from "@/components/TrackCard";
import useMusicPlayer from "@/hooks/useMusicPlayer";
import AlbumCard from "@/components/AlbumCard";
import type { PlaylistInfo } from "@/types/playlist.types";
import { getAllPlaylists } from "@/services/playlistService";

const Home: React.FC = () => {
  const { play } = useMusicPlayer();

  const {
    data: artists,
    isLoading: artistLoading,
    isError: artistError,
  } = useQuery<User[]>({
    queryKey: ["artists"],
    queryFn: getArtists,
  });

  const {
    data: tracks,
    isLoading: trackLoading,
    isError: trackError,
  } = useQuery<TracksForArtist[]>({
    queryKey: ["tracks"],
    queryFn: getAllTracks,
  });

  const {
    data: playlists,
    isLoading: playlistLoading,
    isError: playlistError,
    isSuccess: playlistLoaded
  } = useQuery<PlaylistInfo[]>({
    queryKey: ["playlists"],
    queryFn: getAllPlaylists,
  });

  return (
    <>
      <section className="py-5 px-8 flex flex-col gap-8 max-w-6xl mx-auto">
        <section className="flex flex-col gap-3">
          <h1 className="text-2xl font-extrabold">Playlists</h1>
          <div className="w-full h-fit flex gap-5 py-1 items-center px-1 overflow-x-auto">
            {playlistLoading && <p>Loading...</p>}
            {playlistError && <p>Error...</p>}
            {playlistLoaded && playlists.length === 0 && <p>No playlists found.</p>}
            {playlists?.map((playlist) => (
              <AlbumCard playlist={playlist} />
            ))}
          </div>
        </section>
        <section className="flex flex-col gap-3">
          <h1 className="text-2xl font-extrabold">Artists</h1>
          <div className="w-full h-fit flex gap-5 py-1 items-center px-1 overflow-x-auto">
            {artistLoading && <p>loading...</p>}
            {artistError && <p>Error occured</p>}
            {artists?.map((user) => (
              <ArtistCard key={user.userId} user={user} />
            ))}
          </div>
        </section>
        <section className="flex flex-col gap-1">
          <h1 className="text-2xl font-extrabold">Recent tracks</h1>
          <div className="w-full h-fit flex flex-col overflow-x-auto">
            {trackLoading && <p>loading...</p>}
            {trackError && <p>Error occured</p>}
            {tracks?.map((track) => (
              <TrackCard
                key={track.trackId}
                trackId={track.trackId}
                trackInfo={track.trackInfo}
                playTrack={() => play(track.trackInfo, track.trackId)}
              />
            ))}
          </div>
        </section>
      </section>
    </>
  );
};

export default Home;
