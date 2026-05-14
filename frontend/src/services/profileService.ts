import { apiClient } from './apiClient';
import type { ProfileResponse, UpdateProfileRequest } from '../types/profile';

export const profileService = {
  getMyProfile() {
    return apiClient.get<ProfileResponse>('/profile/me');
  },
  updateMyProfile(payload: UpdateProfileRequest) {
    return apiClient.put<ProfileResponse>('/profile/me', payload);
  },
};
