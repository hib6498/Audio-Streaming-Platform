import { useEffect, useRef } from "react";

interface AddToPlaylistButtonProps {
  setRightOffset: (offset: number) => void;
  onClick: () => void;
}

const AddToPlaylistButton: React.FC<AddToPlaylistButtonProps> = ({
  setRightOffset,
  onClick,
}) => {
  const ref = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (ref.current) {
      const rect = ref.current.getBoundingClientRect();
      setRightOffset(document.documentElement.clientWidth - rect.right);
    }
  }, []);

  return (
    <button
      ref={ref}
      onClick={onClick}
      className="rounded-full flex items-center justify-center size-8 opacity-0 group-hover:opacity-40 cursor-pointer duration-200 hover:opacity-80 hover:bg-gray-200/10 text-xl font-bold"
    >
      <span className="text-center box-content leading-0">+</span>
    </button>
  );
};

export default AddToPlaylistButton;