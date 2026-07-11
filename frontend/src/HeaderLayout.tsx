import { Outlet } from "react-router-dom";
import Header from "./pages/common/Header";
import { MusicPlayer } from "./pages/common/MusicPlayer";

const HeaderLayout: React.FC = () => {
  return (
    <div className="h-full">
      <Header />
      <div className="fixed z-1 left-0 right-0 top-20 bottom-18 overflow-auto">
        <Outlet />
      </div>
      <MusicPlayer />
    </div>
  );
};

export default HeaderLayout;
