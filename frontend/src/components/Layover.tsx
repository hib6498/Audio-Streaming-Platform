import type { MouseEventHandler, ReactNode } from "react";
import CloseButton from "./CloseButton";

interface LayoverProps {
  children?: ReactNode;
  closeCallback: MouseEventHandler
}

const Layover: React.FC<LayoverProps> = ({ children, closeCallback }) => {
  return <div className="absolute flex flex-col inset-0 z-50">
    <div className="absolute size-full bg-red-50 opacity-40"></div>
    <CloseButton onClick={closeCallback} className="size-12 p-1 rounded-full invert-100 border self-end m-10 hover:bg-gray-800 hover:opacity-50 cursor-pointer"/>
    {children}
    </div>;
};

export default Layover;
