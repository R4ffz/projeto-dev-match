import { apiClient } from './apiClient';
import type { SkillResponse } from '../types/skill';

export const skillService = {
  listAll() {
    return apiClient.get<SkillResponse[]>('/skills');
  },
};
