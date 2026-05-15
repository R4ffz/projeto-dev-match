package com.devmatch.profile.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.dto.SkillResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProfileResponse(
    Long id,
    Long userId,
    String name,
    String email,
    Seniority seniority,
    BigDecimal desiredSalary,
    Set<WorkMode> preferredWorkModes,
    String professionalSummary,
    List<SkillResponse> skills
) {}
