package com.devmatch.match.dto;

import java.util.List;

public record MatchResult(
    Long jobId,
    int finalScore,
    int skillsScore,
    int seniorityScore,
    int workModeScore,
    int salaryScore,
    List<String> matchedSkills,
    List<String> missingSkills,
    String explanation
) {}
