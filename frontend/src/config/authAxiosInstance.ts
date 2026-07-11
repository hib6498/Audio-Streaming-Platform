/* eslint-disable @typescript-eslint/no-explicit-any */
import axios, { AxiosError, type AxiosInstance } from "axios";
import { handleTokenRefresh } from "@/utils/tokenManager";

let logoutHandler: (() => void) | null = null;

const setLogoutHandler = (handler: () => void) => {
  logoutHandler = handler;
};

const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_AUTH_API_BASE_URL,
});

instance.interceptors.request.use((config) => {
  const token = localStorage.getItem("aT");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

instance.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<any>) => {
    if (!error.response || error.response.status !== 401) {
      return Promise.reject(error);
    }

    const originalRequest = error.config as any;
    if (!originalRequest || originalRequest._retry) {
      logoutHandler?.();
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      await handleTokenRefresh();

      // attach new token explicitly
      const newToken = localStorage.getItem("aT");
      if (newToken) {
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
      }

      return instance(originalRequest);
    } catch (err) {
      return Promise.reject(err);
    }
  },
);

export { instance, logoutHandler, setLogoutHandler };
