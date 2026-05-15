package com.devmatch.profile.dto;

import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record UpdateProfileRequest(
    @NotNull Seniority seniority,
    @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal desiredSalary,
    @NotEmpty Set<WorkMode> preferredWorkModes,
    @Size(max = 2000) String professionalSummary,
    @NotNull List<String> skillNames
) {}
