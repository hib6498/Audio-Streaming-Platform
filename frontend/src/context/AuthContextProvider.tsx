import { useCallback, useEffect, useState, type ReactNode } from "react";
import type { User } from "@/types/auth.type";
import { useNavigate } from "react-router-dom";
import { setLogoutHandler } from "@/config/authAxiosInstance";
import AuthContext from "./AuthContext";

import { getUser, refreshToken } from "@/services/authService";
import { changeProfilePicUrl } from "@/utils/userUtils";

const AuthContextProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const navigate = useNavigate();

  const login = useCallback(
    async (token: string, navigateTo: string | null): Promise<void> => {
      localStorage.setItem("aT", token);

      const user = await getUser();
      changeProfilePicUrl(user);
      localStorage.setItem("cache-user", JSON.stringify(user));
      setUser(user);
      if (navigateTo) navigate(navigateTo);
    },
    [navigate],
  );

  const logout = useCallback(() => {
    localStorage.removeItem("aT");
    localStorage.removeItem("cache-user");
    setUser(null);
    navigate("/login");
  }, [navigate]);

  const refresh = useCallback(async () => {
    try {
      const res = await refreshToken();
      const accessToken = res.accessToken;
      await login(accessToken, null);
    } catch {
      logout();
    }
  }, [login, logout]);

  useEffect(() => setLogoutHandler(logout), [logout]);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem("aT");
      const cachedUser = localStorage.getItem("cache-user");

      if (cachedUser) {
        setUser(JSON.parse(cachedUser));
        return;
      }

      if (token?.length) {
        await login(token, null);
        return;
      }

      if (token) await refresh();
    };

    initAuth();
  }, [login, refresh]);

  return (
    <AuthContext.Provider value={{ user, setUser, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContextProvider;
