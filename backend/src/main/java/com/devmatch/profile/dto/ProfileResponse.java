package com.devmatch.profile.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.dto.SkillResponse;

import java.math.BigDecimal;
import java.util.List;

public record ProfileResponse(
    Long id,
    Long userId,
    String name,
    String email,
    Seniority seniority,
    BigDecimal desiredSalary,
    WorkMode preferredWorkMode,
    String professionalSummary,
    List<SkillResponse> skills
) {}
