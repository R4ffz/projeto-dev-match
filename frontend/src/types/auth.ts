export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UserSummary {
  id: number;
  name: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresInHours: number;
  user: UserSummary;
}
