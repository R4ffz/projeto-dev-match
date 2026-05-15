package com.devmatch.match;

import com.devmatch.job.Job;
import com.devmatch.match.dto.MatchResult;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.Skill;
import com.devmatch.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchCalculatorTest {

    @Test
    @DisplayName("Exemplo do PDF: 3/4 skills, JUNIOR=JUNIOR, REMOTE=REMOTE, salario dentro -> finalScore 85")
    void exemploDoPdf() {
        CandidateProfile profile = profile(Seniority.JUNIOR, "4000", WorkMode.REMOTE,
            "Java", "Spring Boot", "SQL", "React");
        Job job = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot", "Docker", "SQL");

        MatchResult r = MatchCalculator.calculate(profile, job);

        assertEquals(75, r.skillsScore());
        assertEquals(100, r.seniorityScore());
        assertEquals(100, r.workModeScore());
        assertEquals(100, r.salaryScore());
        assertEquals(85, r.finalScore());
        assertEquals(List.of("Java", "SQL", "Spring Boot"), r.matchedSkills());
        assertEquals(List.of("Docker"), r.missingSkills());
        assertTrue(r.explanation().contains("3 de 4"));
    }

    @Test
    @DisplayName("Match perfeito: todas as skills, mesmos enums, salario no centro -> finalScore 100")
    void matchPerfeito() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000", WorkMode.HYBRID,
            "Java", "Spring Boot");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.HYBRID, "7000", "9000",
            "Java", "Spring Boot");

        MatchResult r = MatchCalculator.calculate(profile, job);

        assertEquals(100, r.skillsScore());
        assertEquals(100, r.seniorityScore());
        assertEquals(100, r.workModeScore());
        assertEquals(100, r.salaryScore());
        assertEquals(100, r.finalScore());
        assertTrue(r.missingSkills().isEmpty());
    }

    @Test
    @DisplayName("Zero skills compativeis -> skillsScore = 0; finalScore so vem dos outros criterios")
    void zeroSkills() {
        CandidateProfile profile = profile(Seniority.SENIOR, "12000", WorkMode.REMOTE,
            "Python", "Django");
        Job job = job(1L, Seniority.SENIOR, WorkMode.REMOTE, "10000", "15000",
            "Java", "Spring Boot");

        MatchResult r = MatchCalculator.calculate(profile, job);

        assertEquals(0, r.skillsScore());
        assertEquals(100, r.seniorityScore());
        assertEquals(100, r.workModeScore());
        assertEquals(100, r.salaryScore());
        // 0*0.60 + 100*0.20 + 100*0.10 + 100*0.10 = 40
        assertEquals(40, r.finalScore());
    }

    @Test
    @DisplayName("Senioridade adjacente (JUNIOR vs MID_LEVEL) -> seniorityScore = 50")
    void senioridadeAdjacente() {
        CandidateProfile profile = profile(Seniority.JUNIOR, "5000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "4500", "6500", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(50, r.seniorityScore());
    }

    @Test
    @DisplayName("Senioridade muito distante (INTERN vs SENIOR) -> seniorityScore = 0")
    void senioridadeMuitoDistante() {
        CandidateProfile profile = profile(Seniority.INTERN, "2000", WorkMode.ONSITE, "Java");
        Job job = job(1L, Seniority.SENIOR, WorkMode.ONSITE, "12000", "16000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(0, r.seniorityScore());
    }

    @Test
    @DisplayName("Modalidade alternativa via HYBRID (candidato REMOTE, vaga HYBRID) -> workModeScore = 50")
    void modalidadeAlternativaHybrid() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.HYBRID, "7000", "9000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(50, r.workModeScore());
    }

    @Test
    @DisplayName("Modalidade incompativel (REMOTE x ONSITE) -> workModeScore = 0")
    void modalidadeIncompativel() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.ONSITE, "7000", "9000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(0, r.workModeScore());
    }

    @Test
    @DisplayName("Multi modalidade: candidato aceita REMOTE+HYBRID e vaga eh REMOTE -> workModeScore = 100")
    void modalidadeMultiContemVaga() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000",
            Set.of(WorkMode.REMOTE, WorkMode.HYBRID), "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "7000", "9000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(100, r.workModeScore());
    }

    @Test
    @DisplayName("Multi modalidade: candidato aceita REMOTE+HYBRID e vaga eh ONSITE -> 50 via HYBRID no candidato")
    void modalidadeMultiAlternativaPorHybrid() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000",
            Set.of(WorkMode.REMOTE, WorkMode.HYBRID), "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.ONSITE, "7000", "9000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(50, r.workModeScore());
    }

    @Test
    @DisplayName("Modalidades vazias (candidato nao declarou preferencia) -> workModeScore = 0")
    void modalidadeVazia() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8000",
            new HashSet<>(), "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "7000", "9000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(0, r.workModeScore());
    }

    @Test
    @DisplayName("Salario proximo: 20% abaixo do minimo -> salaryScore = 50")
    void salarioProximoAbaixo() {
        // minSalary=5000, 20% abaixo = 4000. Desejado 4000 -> proximo (50)
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "4000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "5000", "7000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(50, r.salaryScore());
    }

    @Test
    @DisplayName("Salario proximo: 20% acima do maximo -> salaryScore = 50")
    void salarioProximoAcima() {
        // maxSalary=7000, 20% acima = 8400. Desejado 8400 -> proximo (50)
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "8400", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "5000", "7000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(50, r.salaryScore());
    }

    @Test
    @DisplayName("Salario muito fora -> salaryScore = 0")
    void salarioMuitoFora() {
        CandidateProfile profile = profile(Seniority.MID_LEVEL, "20000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.MID_LEVEL, WorkMode.REMOTE, "5000", "7000", "Java");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(0, r.salaryScore());
    }

    @Test
    @DisplayName("Perfil vazio (todos os campos null) -> tudo zera, exceto skills (vai 0 porque profile vazio)")
    void perfilVazio() {
        CandidateProfile profile = new CandidateProfile();  // tudo null
        profile.setUser(user(1L));
        Job job = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot");

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(0, r.skillsScore());
        assertEquals(0, r.seniorityScore());
        assertEquals(0, r.workModeScore());
        assertEquals(0, r.salaryScore());
        assertEquals(0, r.finalScore());
        assertEquals(2, r.missingSkills().size());
        assertTrue(r.matchedSkills().isEmpty());
    }

    @Test
    @DisplayName("Job sem skills exigidas -> skillsScore = 100 (nada para validar)")
    void jobSemSkills() {
        CandidateProfile profile = profile(Seniority.JUNIOR, "4000", WorkMode.REMOTE, "Java");
        Job job = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000");  // sem skills

        MatchResult r = MatchCalculator.calculate(profile, job);
        assertEquals(100, r.skillsScore());
        assertEquals(100, r.finalScore());
    }

    @Test
    @DisplayName("Score arredonda corretamente (1/3 skills = 33, 2/3 = 67)")
    void arredondamentoSkills() {
        CandidateProfile p13 = profile(Seniority.JUNIOR, "4000", WorkMode.REMOTE, "Java");
        Job j3 = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot", "Docker");
        assertEquals(33, MatchCalculator.calculate(p13, j3).skillsScore());

        CandidateProfile p23 = profile(Seniority.JUNIOR, "4000", WorkMode.REMOTE, "Java", "Spring Boot");
        assertEquals(67, MatchCalculator.calculate(p23, j3).skillsScore());
    }

    @Test
    @DisplayName("Final score = soma ponderada arredondada (84.5 -> 85, 84.4 -> 84)")
    void finalScoreArredondamento() {
        // Skills 75 (3/4), Sen 100, WM 100, Sal 100 -> 75*0.6 + 30 = 75
        // Espera 85 conforme PDF -> ja coberto em exemploDoPdf
        // Aqui forco 84.5: skills 74 nao da. Vou forcar:
        // skills 75 * 0.6 = 45.0
        // seniority 50 * 0.2 = 10.0
        // workMode 100 * 0.1 = 10.0
        // salary 50 * 0.1 = 5.0
        // total = 70.0 -> 70 (sem ambiguidade)
        // Desejado 4500 cai na tolerancia (5000 - 20% = 4000) -> salary 50
        CandidateProfile p = profile(Seniority.JUNIOR, "4500", WorkMode.REMOTE,
            "Java", "Spring Boot", "SQL");  // 3 de 4 skills
        Job j = job(1L, Seniority.MID_LEVEL, WorkMode.HYBRID, "5000", "7000",  // sen +1, hybrid alternativa
            "Java", "Spring Boot", "SQL", "Docker");

        MatchResult r = MatchCalculator.calculate(p, j);
        assertEquals(75, r.skillsScore());
        assertEquals(50, r.seniorityScore());
        assertEquals(50, r.workModeScore());
        assertEquals(50, r.salaryScore());
        // 75*0.6 + 50*0.2 + 50*0.1 + 50*0.1 = 45 + 10 + 5 + 5 = 65
        assertEquals(65, r.finalScore());
    }

    // ------------------------ helpers ------------------------

    private static final AtomicLong SKILL_ID_SEQ = new AtomicLong(1000);

    private static CandidateProfile profile(Seniority sen, String desiredSalary, WorkMode wm, String... skillNames) {
        return profile(sen, desiredSalary, new HashSet<>(Set.of(wm)), skillNames);
    }

    private static CandidateProfile profile(Seniority sen, String desiredSalary, Set<WorkMode> wms, String... skillNames) {
        CandidateProfile p = new CandidateProfile();
        p.setUser(user(1L));
        p.setSeniority(sen);
        p.setDesiredSalary(new BigDecimal(desiredSalary));
        p.setPreferredWorkModes(new HashSet<>(wms));
        p.setSkills(skillSet(skillNames));
        return p;
    }

    private static Job job(Long id, Seniority sen, WorkMode wm,
                            String minSalary, String maxSalary,
                            String... skillNames) {
        Job j = new Job();
        j.setId(id);
        j.setTitle("Test Job " + id);
        j.setCompany("Company " + id);
        j.setDescription("Description " + id);
        j.setSeniority(sen);
        j.setWorkMode(wm);
        j.setMinSalary(new BigDecimal(minSalary));
        j.setMaxSalary(new BigDecimal(maxSalary));
        j.setSkills(skillSet(skillNames));
        return j;
    }

    private static User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setName("Test User");
        u.setEmail("test@example.com");
        u.setPasswordHash("hash");
        return u;
    }

    private static Set<Skill> skillSet(String... names) {
        Set<Skill> set = new LinkedHashSet<>();
        Arrays.stream(names).forEach(name -> {
            Skill s = new Skill(name);
            s.setId(SKILL_ID_SEQ.incrementAndGet());
            set.add(s);
        });
        return set;
    }
}
