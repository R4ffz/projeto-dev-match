package com.devmatch.profile.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProfileRequest(
    @NotNull Seniority seniority,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal desiredSalary,
    @NotNull WorkMode preferredWorkMode,
    @Size(max = 2000) String professionalSummary,
    @NotNull List<String> skillNames
) {}
