import axios, { AxiosError, type AxiosInstance } from "axios";
import { handleTokenRefresh } from "@/utils/tokenManager.ts";
import type { CustomInternalAxiosRequestConfig } from "@/types/CustomInternalAxiosRequestConfig";

const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_AUDIO_API_BASE_URL,
  withCredentials: true,
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
  async (error: AxiosError) => {
    if (!error.response || error.response.status !== 401) {
      return Promise.reject(error);
    }

    const originalRequest = error.config as CustomInternalAxiosRequestConfig;

    if (!originalRequest || originalRequest._retry) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      await handleTokenRefresh();

      const newToken = localStorage.getItem("aT");
      if (newToken) {
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
      }

      return instance(originalRequest);
    } catch (err) {
      return Promise.reject(err);
    }
  }
);

export { instance };
