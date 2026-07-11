import { cn } from "@sglara/cn";
import cloudLogo from "@/assets/cloudmark-white-transparent.png";
import { useNavigate } from "react-router-dom";

interface SoundcloudLogoProps {
  className?: string;
  redirect?: boolean;
}

const SoundcloudLogo: React.FC<SoundcloudLogoProps> = ({
  className,
  redirect,
}) => {
  const navigate = useNavigate();

  return (
    <img
      src={cloudLogo}
      alt="Soundcloud Logo"
      className={cn("cursor-pointer", className)}
      onClick={redirect ? () => navigate("/") : undefined}
    />
  );
};

export default SoundcloudLogo;
