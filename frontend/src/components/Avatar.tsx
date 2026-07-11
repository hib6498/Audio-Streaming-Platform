import { cn } from "@sglara/cn";
import colorFromName from "@/lib/colorFromName";
import type { User } from "@/types/auth.type";
import { useEffect, useRef } from "react";

type AvatarSize = "small" | "medium" | "large";

interface AvatarIconProps {
  size?: AvatarSize;
  withHover?: boolean;
  callback?: () => void;
  setDropDownRightBound?: (val: number) => void
  user: User;
}

const sizeMap: Record<
  AvatarSize,
  {
    container: string;
    text: string;
  }
> = {
  small: {
    container: "size-10",
    text: "text-xl",
  },
  medium: {
    container: "size-15",
    text: "text-3xl",
  },
  large: {
    container: "size-45",
    text: "text-6xl",
  },
};

const Avatar: React.FC<AvatarIconProps> = ({
  size = "small",
  withHover = true,
  callback,
  setDropDownRightBound,
  user,
}) => {
  const ref = useRef<HTMLDivElement>(null);
  const initial = user.displayName[0]?.toUpperCase() ?? "";;
  const { container, text } = sizeMap[size] || sizeMap.small;

  useEffect(() => {
    if (ref.current) {
      setDropDownRightBound?.(ref.current.getBoundingClientRect().right);
    }

  }, [setDropDownRightBound])


  return (
    <div
      onClick={callback}
      ref={ref}
      className={cn(
        "flex items-center justify-center text-center rounded-full overflow-hidden bg-cover ring-gray-500 dark:ring-white",
        container,
        withHover && "cursor-pointer hover:ring-1 focus:ring-1"
      )}
      style={{
         backgroundColor: colorFromName(initial),
      }}
    >
      {!user.profilePic ? (
        <h1 className={cn("font-bold text-white pl-0.5", text)}>{initial}</h1>
      ) : (
        <img
          src={user.profilePic}
          alt="User Profile Picture"
          className="object-cover w-full h-full"
        />
      )}
    </div>
  );
};

export default Avatar;