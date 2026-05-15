package com.devmatch.match;

import com.devmatch.job.Job;
import com.devmatch.match.dto.MatchResult;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.Skill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pure scoring logic. No Spring, no JPA — easy to unit-test and explain.
 *
 * Weights (per PDF):
 *   skills 60%, seniority 20%, workMode 10%, salary 10%
 */
public final class MatchCalculator {

    public static final double WEIGHT_SKILLS = 0.60;
    public static final double WEIGHT_SENIORITY = 0.20;
    public static final double WEIGHT_WORK_MODE = 0.10;
    public static final double WEIGHT_SALARY = 0.10;

    private static final BigDecimal SALARY_TOLERANCE = new BigDecimal("0.20");

    private MatchCalculator() {}

    public static MatchResult calculate(CandidateProfile profile, Job job) {
        SkillsBreakdown sb = scoreSkills(profile.getSkills(), job.getSkills());
        int seniorityScore = scoreSeniority(profile.getSeniority(), job.getSeniority());
        int workModeScore = scoreWorkMode(profile.getPreferredWorkModes(), job.getWorkMode());
        int salaryScore = scoreSalary(profile.getDesiredSalary(), job.getMinSalary(), job.getMaxSalary());

        int finalScore = (int) Math.round(
            sb.score() * WEIGHT_SKILLS
                + seniorityScore * WEIGHT_SENIORITY
                + workModeScore * WEIGHT_WORK_MODE
                + salaryScore * WEIGHT_SALARY
        );

        String explanation = buildExplanation(sb, seniorityScore, workModeScore, salaryScore);

        return new MatchResult(
            job.getId(),
            finalScore,
            sb.score(),
            seniorityScore,
            workModeScore,
            salaryScore,
            sb.matched(),
            sb.missing(),
            explanation
        );
    }

    // ---------- Skills: 60% ----------
    private static SkillsBreakdown scoreSkills(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (jobSkills == null || jobSkills.isEmpty()) {
            return new SkillsBreakdown(100, List.of(), List.of());
        }
        Set<String> candidateNames = candidateSkills == null
            ? Set.of()
            : candidateSkills.stream().map(Skill::getName).collect(Collectors.toSet());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (Skill js : jobSkills) {
            if (candidateNames.contains(js.getName())) {
                matched.add(js.getName());
            } else {
                missing.add(js.getName());
            }
        }
        Collections.sort(matched);
        Collections.sort(missing);

        int score = (int) Math.round(100.0 * matched.size() / jobSkills.size());
        return new SkillsBreakdown(score, matched, missing);
    }

    // ---------- Seniority: 20% ----------
    // diff = 0 -> 100; diff = 1 -> 50; diff >= 2 -> 0 (INTERN=0, JUNIOR=1, MID=2, SENIOR=3)
    private static int scoreSeniority(Seniority candidate, Seniority job) {
        if (candidate == null || job == null) return 0;
        int diff = Math.abs(candidate.ordinal() - job.ordinal());
        return switch (diff) {
            case 0 -> 100;
            case 1 -> 50;
            default -> 0;
        };
    }

    // ---------- WorkMode: 10% ----------
    // candidato contem a modalidade da vaga -> 100;
    // HYBRID em qualquer lado (candidato ou vaga) -> 50 (flexibilidade);
    // sem interseccao e sem HYBRID -> 0.
    private static int scoreWorkMode(Set<WorkMode> candidate, WorkMode job) {
        if (candidate == null || candidate.isEmpty() || job == null) return 0;
        if (candidate.contains(job)) return 100;
        if (candidate.contains(WorkMode.HYBRID) || job == WorkMode.HYBRID) return 50;
        return 0;
    }

    // ---------- Salary: 10% ----------
    // dentro [min, max] -> 100; ate 20% abaixo de min ou acima de max -> 50; alem -> 0
    private static int scoreSalary(BigDecimal desired, BigDecimal min, BigDecimal max) {
        if (desired == null || min == null || max == null) return 0;
        if (desired.compareTo(min) >= 0 && desired.compareTo(max) <= 0) return 100;
        BigDecimal lower = min.subtract(min.multiply(SALARY_TOLERANCE));
        BigDecimal upper = max.add(max.multiply(SALARY_TOLERANCE));
        if (desired.compareTo(lower) >= 0 && desired.compareTo(upper) <= 0) return 50;
        return 0;
    }

    // ---------- Explanation ----------
    private static String buildExplanation(SkillsBreakdown sb, int seniority, int workMode, int salary) {
        int total = sb.matched().size() + sb.missing().size();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Voce atende ").append(sb.matched().size()).append(" de ").append(total)
            .append(" skills exigidas. ");
        sb2.append(switch (seniority) {
            case 100 -> "Senioridade compativel. ";
            case 50 -> "Senioridade proxima. ";
            default -> "Senioridade muito distante. ";
        });
        sb2.append(switch (workMode) {
            case 100 -> "Modalidade igual. ";
            case 50 -> "Modalidade alternativa aceitavel. ";
            default -> "Modalidade incompativel. ";
        });
        sb2.append(switch (salary) {
            case 100 -> "Pretensao salarial dentro da faixa.";
            case 50 -> "Pretensao salarial proxima da faixa.";
            default -> "Pretensao salarial fora da faixa.";
        });
        return sb2.toString();
    }

    private record SkillsBreakdown(int score, List<String> matched, List<String> missing) {}
}
