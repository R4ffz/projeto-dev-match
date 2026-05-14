import { apiClient } from './apiClient';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/auth';

export const authService = {
  register(payload: RegisterRequest) {
    return apiClient.post<AuthResponse>('/auth/register', payload);
  },
  login(payload: LoginRequest) {
    return apiClient.post<AuthResponse>('/auth/login', payload);
  },
};
