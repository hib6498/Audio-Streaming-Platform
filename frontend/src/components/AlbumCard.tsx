import type { PlaylistInfo } from "@/types/playlist.types";
import { useNavigate } from "react-router-dom";

interface AlbumCardProps {
  playlist: PlaylistInfo;
}

const AlbumCard: React.FC<AlbumCardProps> = ({ playlist }) => {
  const navigate = useNavigate();
  const playlistPage = `/playlist/${playlist.id}`;

  return (
    <div
      onClick={() => navigate(playlistPage)}
      className="flex flex-col items-center max-w-44 gap-2 p-2 rounded-sm box-content hover:bg-gray-100/5 cursor-pointer"
    >
      <div className="size-40 border border-white/40 rounded-sm items-center justify-center flex">
        <img src={playlist.coverArt} alt="Cover Art" />
      </div>
      <h1 className="text-wrap line-clamp-2 truncate leading-snug text-center">
        {playlist.title}
      </h1>
    </div>
  );
};

export default AlbumCard;
