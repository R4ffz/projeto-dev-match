package com.devmatch.job;

import com.devmatch.job.dto.JobFilter;
import com.devmatch.skill.Skill;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class JobSpecifications {

    private JobSpecifications() {}

    public static Specification<Job> build(JobFilter filter) {
        Specification<Job> spec = Specification.where(null);

        if (hasText(filter.keyword())) {
            String pattern = "%" + filter.keyword().toLowerCase().trim() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("company")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            ));
        }

        if (filter.seniority() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("seniority"), filter.seniority()));
        }

        if (filter.workMode() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("workMode"), filter.workMode()));
        }

        if (filter.minSalary() != null) {
            // Vagas cujo teto seja maior ou igual ao salario desejado
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("maxSalary"), filter.minSalary()));
        }

        if (hasText(filter.skill())) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                Join<Job, Skill> join = root.join("skills");
                return cb.equal(join.get("name"), filter.skill().trim());
            });
        }

        return spec;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
