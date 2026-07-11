import type { ButtonHTMLAttributes, ReactNode } from "react";
import { cn } from "@sglara/cn";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
}

const ActionButton: React.FC<ButtonProps> = ({
  children,
  className,
  disabled,
  ...props
}) => {
  return (
    <button
      disabled={disabled}
      className={cn(
        "block border bg-white text-black rounded-full py-2 w-full text-lg cursor-pointer hover:bg-transparent hover:text-white duration-200 disabled:opacity-50 disabled:cursor-not-allowed",
        className,
      )}
      {...props}
    >
      {children}
    </button>
  );
};

export default ActionButton;
