import type { Seniority, WorkMode } from './profile';
import type { SkillResponse } from './skill';

export interface JobResponse {
  id: number;
  title: string;
  company: string;
  description: string;
  seniority: Seniority;
  workMode: WorkMode;
  minSalary: number;
  maxSalary: number;
  skills: SkillResponse[];
}

export interface JobFilter {
  keyword?: string;
  seniority?: Seniority;
  workMode?: WorkMode;
  minSalary?: number;
  skill?: string;
}
