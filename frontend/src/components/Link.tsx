import { useNavigate } from "react-router-dom";

interface LinkProps {
  url: string;
  children: React.ReactNode;
}

const Link = ({ url, children }: LinkProps) => {
  const navigate = useNavigate();

  return (
    <span
      onClick={() => navigate(url)}
      className="underline text-blue-400 underline-offset-4 cursor-pointer hover:text-blue-300"
    >
      {children}
    </span>
  );
};

export default Link;