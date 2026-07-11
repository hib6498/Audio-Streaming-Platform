import { useContext } from "react";
import AuthContext from "@/context/AuthContext";
import type { AuthContextType } from "@/types/auth.type";

const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthContextProvider");
  }

  return context;
};

export default useAuth;
