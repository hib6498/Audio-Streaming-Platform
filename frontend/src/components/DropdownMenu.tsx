import { useRef, useEffect } from "react";
import { cn } from "@sglara/cn";

interface DropdownMenuProps extends React.HTMLAttributes<HTMLDivElement> {
  showDropdown: boolean;
  closeDropdown: () => void;
  children?: React.ReactNode;
}

const DropdownMenu: React.FC<DropdownMenuProps> = ({
  showDropdown,
  closeDropdown: setShowDropdown,
  className,
  children,
  ...props
}) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!showDropdown) return;

    const handleClickOutside = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node)
      ) {
        setShowDropdown();
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showDropdown]);

  return (
    <div
      ref={dropdownRef}
      className={cn("absolute bg-black rounded-lg shadow-lg z-50", className)}
      {...props}
    >
      {children}
    </div>
  );
};

export default DropdownMenu;
