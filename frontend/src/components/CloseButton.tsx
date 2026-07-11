import crossSVG from "@/assets/cross-white.svg";
import type { ImgHTMLAttributes } from "react";

interface CloseButtonProps extends ImgHTMLAttributes<HTMLImageElement> {
  className?: string;
}

const CloseButton: React.FC<CloseButtonProps> = ({ className, ...props }) => {
  return <img {...props} className={className} src={crossSVG} alt="Close" />;
};

export default CloseButton;
