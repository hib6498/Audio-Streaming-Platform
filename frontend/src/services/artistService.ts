import { instance as audioAxiosInstance } from "@/config/trackAxiosInstance";
import { instance as authAxiosInstance } from "@/config/authAxiosInstance";
import type { User } from "@/types/auth.type";
import { changeProfilePicUrl } from "@/utils/userUtils";

const getArtists = async (): Promise<User[]> => {
  const { data } = await audioAxiosInstance.get<User[]>("/user/all");

  data.forEach((user) => changeProfilePicUrl(user));
  return data;
};

const getArtist = async (id: number): Promise<User> => {
  const { data } = await authAxiosInstance.get<User>(`/user/${id}`);

  changeProfilePicUrl(data);
  return data;
};

export { getArtists, getArtist };
