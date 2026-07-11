import type { InternalAxiosRequestConfig } from "axios";

interface CustomInternalAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

export type { CustomInternalAxiosRequestConfig };
