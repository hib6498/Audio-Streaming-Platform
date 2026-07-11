import { instance as axiosInstance } from "../config/authAxiosInstance";
import type { User } from "../types/auth.type";
import type {
  LoginResponse,
  LoginInfo,
  RegisterInfo,
  RegisterResponse,
  RefreshTokenResponse,
} from "../types/authDTO.types";

const baseURL = import.meta.env.VITE_AUTH_API_BASE_URL;

const registerUser = async (
  registerInfo: RegisterInfo,
): Promise<RegisterResponse> => {
  const { data } = await axiosInstance.post<RegisterResponse>(
    "/user/register",
    registerInfo,
  );
  return data;
};

const loginUser = async (loginInfo: LoginInfo): Promise<LoginResponse> => {
  const { data } = await axiosInstance.post<LoginResponse>(
    "/user/login",
    loginInfo,
    { withCredentials: true },
  );

  return data;
};

const refreshToken = async (): Promise<RefreshTokenResponse> => {
  //fetch used to prevent infinite loop in interceptors
  const response = await fetch(`${baseURL}/user/refresh`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("Failed to refresh token");
  }

  const data: RefreshTokenResponse = await response.json();

  localStorage.setItem("aT", data.accessToken);
  return data;
};

const getUser = async (): Promise<User> => {
  const { data } = await axiosInstance.get<User>("/user/me");
  return data;
};

export { loginUser, registerUser, getUser, refreshToken };
