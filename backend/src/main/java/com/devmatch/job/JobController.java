package com.devmatch.job;

import com.devmatch.job.dto.JobFilter;
import com.devmatch.job.dto.JobResponse;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Vagas tech com filtros e detalhamento")
public class JobController {

    private final JobService jobService;

    @GetMapping
    @Operation(summary = "Lista vagas com filtros opcionais")
    public List<JobResponse> list(
        @Parameter(description = "Busca em titulo, empresa e descricao (case-insensitive)")
        @RequestParam(required = false) String keyword,
        @Parameter(description = "INTERN, JUNIOR, MID_LEVEL ou SENIOR")
        @RequestParam(required = false) Seniority seniority,
        @Parameter(description = "REMOTE, HYBRID ou ONSITE")
        @RequestParam(required = false) WorkMode workMode,
        @Parameter(description = "Considera vagas cujo teto salarial seja >= este valor")
        @RequestParam(required = false) BigDecimal minSalary,
        @Parameter(description = "Nome exato de uma skill exigida pela vaga")
        @RequestParam(required = false) String skill
    ) {
        return jobService.search(new JobFilter(keyword, seniority, workMode, minSalary, skill));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha uma vaga pelo id")
    public JobResponse getById(@PathVariable Long id) {
        return jobService.findById(id);
    }
}
