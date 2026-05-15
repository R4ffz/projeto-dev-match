import type { SkillResponse } from './skill';

export type Seniority = 'INTERN' | 'JUNIOR' | 'MID_LEVEL' | 'SENIOR';
export type WorkMode = 'REMOTE' | 'HYBRID' | 'ONSITE';

export const SENIORITIES: Seniority[] = ['INTERN', 'JUNIOR', 'MID_LEVEL', 'SENIOR'];
export const WORK_MODES: WorkMode[] = ['REMOTE', 'HYBRID', 'ONSITE'];

export interface ProfileResponse {
  id: number;
  userId: number;
  name: string;
  email: string;
  seniority: Seniority | null;
  desiredSalary: number | null;
  preferredWorkModes: WorkMode[];
  professionalSummary: string | null;
  skills: SkillResponse[];
}

export interface UpdateProfileRequest {
  seniority: Seniority;
  desiredSalary: number;
  preferredWorkModes: WorkMode[];
  professionalSummary: string | null;
  skillNames: string[];
}
