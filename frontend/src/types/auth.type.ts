interface User {
  userId: number,
  username: string;
  email: string;
  displayName: string;
  profilePic: string;
}

interface AuthContextType {
  user: User | null;
  setUser: (user: User) => void;
  login: (token: string, navigateTo: string | null) => Promise<void>;
  logout: () => void;
}

export type { User, AuthContextType };
