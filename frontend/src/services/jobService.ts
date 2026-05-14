import { apiClient, buildQueryString } from './apiClient';
import type { JobFilter, JobResponse } from '../types/job';
import type { MatchResult, RecommendedJob } from '../types/match';

export const jobService = {
  list(filter: JobFilter = {}) {
    const qs = buildQueryString({
      keyword: filter.keyword,
      seniority: filter.seniority,
      workMode: filter.workMode,
      minSalary: filter.minSalary,
      skill: filter.skill,
    });
    return apiClient.get<JobResponse[]>(`/jobs${qs}`);
  },
  getById(id: number) {
    return apiClient.get<JobResponse>(`/jobs/${id}`);
  },
  listRecommended() {
    return apiClient.get<RecommendedJob[]>('/jobs/recommended');
  },
  getMatch(id: number) {
    return apiClient.get<MatchResult>(`/jobs/${id}/match`);
  },
};
