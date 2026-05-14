package com.devmatch.job;

import com.devmatch.job.dto.JobResponse;
import com.devmatch.skill.Skill;
import com.devmatch.skill.dto.SkillResponse;

import java.util.Comparator;
import java.util.List;

public final class JobMapper {

    private JobMapper() {}

    public static JobResponse toResponse(Job job) {
        List<SkillResponse> skills = job.getSkills().stream()
            .sorted(Comparator.comparing(Skill::getName))
            .map(s -> new SkillResponse(s.getId(), s.getName()))
            .toList();
        return new JobResponse(
            job.getId(),
            job.getTitle(),
            job.getCompany(),
            job.getDescription(),
            job.getSeniority(),
            job.getWorkMode(),
            job.getMinSalary(),
            job.getMaxSalary(),
            skills
        );
    }
}
