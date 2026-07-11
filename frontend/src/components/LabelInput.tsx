import type { InputHTMLAttributes, JSX } from "react";
import Input from "./Input";

interface LabelInputProps {
  name: string;
  label: string;
  inputProps?: InputHTMLAttributes<HTMLInputElement>
}

const LabelInput = ({ name, label, inputProps }: LabelInputProps): JSX.Element => {
  return (
    <div>
      <h3 className="text-md font-semibold">{label}</h3>
      <Input name={name} {...inputProps}></Input>
    </div>
  );
};

export default LabelInput;
