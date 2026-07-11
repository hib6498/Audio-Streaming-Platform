import type { User } from "@/types/auth.type";
import Avatar from "./Avatar";
import { useNavigate } from "react-router-dom";

interface ArtistCardProps {
  user: User;
}

const ArtistCard: React.FC<ArtistCardProps> = ({ user }) => {
  const navigate = useNavigate();
  const artistPage = `user/${user.userId}`;

  return (
    <div className="flex flex-col items-center max-w-44 gap-2">
      <Avatar
        callback={() => navigate(artistPage, { state: user })}
        user={user}
        size="large"
      ></Avatar>
      <h1 className="text-wrap line-clamp-2 font-semibold hover:opacity-80 cursor-pointer truncate leading-snug text-center">
        {user.displayName}
      </h1>
    </div>
  );
};

export default ArtistCard;
