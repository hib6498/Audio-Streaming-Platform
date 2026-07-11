import { useNavigate } from "react-router-dom";
import SoundcloudLogo from "@/components/SoundcloudLogo";
import ActionButton from "@/components/ActionButton";
import Search from "@/components/Search";
import Avatar from "@/components/Avatar";
import useAuth from "@/hooks/useAuth";
import { useState } from "react";

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [rightOffset, setRightOffset] = useState<number>(0);
  const [showProfileDropdown, setShowProfileDropdown] =
    useState<boolean>(false);

  return (
    <div className="bg-black fixed max-h-20 top-0 right-0 left-0 z-20">
      <div className="flex max-w-6xl shrink-0 justify-between px-5 mx-auto items-center">
        <SoundcloudLogo className="size-20" redirect />
        <Search />
        {!user ? (
          <ActionButton
            className="px-5 w-fit"
            onClick={() => navigate("/login")}
          >
            Log in
          </ActionButton>
        ) : (
          <div className="flex gap-4">
            <button
              type="button"
              className="hover:bg-gray-600/20 rounded-md text-gray-300 hover:text-white font-bold p-2 cursor-pointer"
              onClick={() => navigate("/upload")}
            >
              Upload
            </button>
            <Avatar
              setDropDownRightBound={(val: number) => setRightOffset(val)}
              user={user}
              callback={() => {
                setShowProfileDropdown((prev) => !prev);
              }}
            ></Avatar>
            {showProfileDropdown && (
              <div
                onBlurCapture={() => setShowProfileDropdown(false)}
                className="absolute min-w-50 rounded-md top-20 bg-black border border-white/50 p-4"
                style={{
                  right: `${document.documentElement.clientWidth - rightOffset}px`,
                }}
              >
                <p className="font-bold">{user.displayName}</p>
                <hr className="mt-2" />
                <div className="my-3">
                  <button
                    className="font-semibold hover:opacity-85 cursor-pointer w-full text-left"
                    onClick={() => navigate(`/user/${user.userId}`)}
                  >
                    Profile
                  </button>
                </div>
                <hr className="opacity-40 mb-1" />
                <button
                  onClick={logout}
                  className="text-red-400 hover:bg-gray-900 w-full py-1 rounded-sm duration-200 cursor-pointer"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Header;
