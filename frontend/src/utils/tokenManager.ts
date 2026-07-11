import { refreshToken } from "@/services/authService";
import type { RefreshTokenResponse } from "@/types/authDTO.types";

let isRefreshing = false;

let queue: ((value: RefreshTokenResponse) => void)[] = [];

export const handleTokenRefresh = async (): Promise<RefreshTokenResponse> => {
  if (isRefreshing) {
    return new Promise<RefreshTokenResponse>((resolve) => {
      queue.push(resolve);
    });
  }

  isRefreshing = true;

  try {
    const res = await refreshToken();

    // resolve all queued promises
    queue.forEach((resolve) => resolve(res));
    queue = [];

    return res;
  } finally {
    isRefreshing = false;
  }
};