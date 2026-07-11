import type { JSX, InputHTMLAttributes } from "react";
import { useState } from "react";
import { cn } from "@sglara/cn";

const Input = ({
  className,
  name,
  value,
  onChange,
  ...props
}: InputHTMLAttributes<HTMLInputElement>): JSX.Element => {
  const [internal, setInternal] = useState<string>(
    typeof value === "string" ? value : "",
  );

  return (
    <input
      type="text"
      name={name}
      className={cn(
        "border rounded-md p-2 my-0.5 w-full border-white/50 not-focus:hover:border-white duration-200",
        className,
      )}
      value={internal}
      onChange={(e) => {
        setInternal(e.target.value);
        onChange?.(e);
      }}
      {...props}
    />
  );
};

export default Input;
