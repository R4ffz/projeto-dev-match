import type { JobResponse } from './job';

export interface MatchResult {
  jobId: number;
  finalScore: number;
  skillsScore: number;
  seniorityScore: number;
  workModeScore: number;
  salaryScore: number;
  matchedSkills: string[];
  missingSkills: string[];
  explanation: string;
}

export interface RecommendedJob {
  job: JobResponse;
  finalScore: number;
}
