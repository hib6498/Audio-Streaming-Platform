import type { User } from "@/types/auth.type";

export const changeProfilePicUrl = (user: User) => {
  if (!user.profilePic) return;
  user.profilePic =
    import.meta.env.VITE_AUTH_API_BASE_URL + `/user/${user.userId}/profile-pic`;
};
