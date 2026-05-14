package com.devmatch.job.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.dto.SkillResponse;

import java.math.BigDecimal;
import java.util.List;

public record JobResponse(
    Long id,
    String title,
    String company,
    String description,
    Seniority seniority,
    WorkMode workMode,
    BigDecimal minSalary,
    BigDecimal maxSalary,
    List<SkillResponse> skills
) {}
