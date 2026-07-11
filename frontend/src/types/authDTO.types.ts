interface LoginInfo {
  username: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
}

interface RegisterInfo {
  username: string;
  displayName: string;
  email: string;
  password: string;
  confirmPassword: string;
}

interface RegisterResponse {
  username: string;
  email: string;
  createdAt: number;
}

interface RefreshTokenResponse {
  accessToken: string;
}

export type {
  LoginInfo,
  LoginResponse,
  RegisterInfo,
  RegisterResponse,
  RefreshTokenResponse,
};
