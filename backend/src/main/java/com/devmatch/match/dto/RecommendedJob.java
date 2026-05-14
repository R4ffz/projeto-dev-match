package com.devmatch.match.dto;

import com.devmatch.job.dto.JobResponse;

public record RecommendedJob(JobResponse job, int finalScore) {}
