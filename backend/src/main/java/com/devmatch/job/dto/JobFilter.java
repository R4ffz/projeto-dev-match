package com.devmatch.job.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;

import java.math.BigDecimal;

public record JobFilter(
    String keyword,
    Seniority seniority,
    WorkMode workMode,
    BigDecimal minSalary,
    String skill
) {}
