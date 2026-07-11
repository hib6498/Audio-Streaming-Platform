import { instance as authAxiosInstance } from "@/config/authAxiosInstance";
import { type UpdateProfileInfo } from "@/types/profile.types";

export const updateProfile = async (data: UpdateProfileInfo): Promise<void> => {
  authAxiosInstance.post<void>("/user/update/profile-pic", data, {
    headers: { "Content-Type": "multipart/form-data" },
    withCredentials: true,
  });
};
