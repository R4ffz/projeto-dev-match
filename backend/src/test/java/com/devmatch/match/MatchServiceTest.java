package com.devmatch.match;

import com.devmatch.job.Job;
import com.devmatch.job.JobNotFoundException;
import com.devmatch.job.JobRepository;
import com.devmatch.match.dto.MatchResult;
import com.devmatch.match.dto.RecommendedJob;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.CandidateProfileRepository;
import com.devmatch.profile.ProfileNotFoundException;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.Skill;
import com.devmatch.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private JobRepository jobRepository;
    @Mock private CandidateProfileRepository profileRepository;

    @InjectMocks private MatchService matchService;

    private static final AtomicLong SKILL_ID = new AtomicLong(1);

    private static Skill skill(String name) {
        Skill s = new Skill(name);
        s.setId(SKILL_ID.incrementAndGet());
        return s;
    }

    private static User user(Long id) {
        User u = new User();
        u.setId(id);
        u.setName("Rafael");
        u.setEmail("rafael@example.com");
        u.setPasswordHash("hash");
        return u;
    }

    private static CandidateProfile profile(Long userId, Seniority sen, String desiredSalary,
                                             WorkMode wm, String... skillNames) {
        CandidateProfile p = new CandidateProfile();
        p.setId(10L);
        p.setUser(user(userId));
        p.setSeniority(sen);
        p.setDesiredSalary(new BigDecimal(desiredSalary));
        p.setPreferredWorkModes(new HashSet<>(Set.of(wm)));
        Set<Skill> set = new LinkedHashSet<>();
        for (String n : skillNames) set.add(skill(n));
        p.setSkills(set);
        return p;
    }

    private static Job job(Long id, Seniority sen, WorkMode wm, String minSal, String maxSal, String... skillNames) {
        Job j = new Job();
        j.setId(id);
        j.setTitle("Vaga " + id);
        j.setCompany("Company " + id);
        j.setDescription("Desc " + id);
        j.setSeniority(sen);
        j.setWorkMode(wm);
        j.setMinSalary(new BigDecimal(minSal));
        j.setMaxSalary(new BigDecimal(maxSal));
        Set<Skill> set = new LinkedHashSet<>();
        for (String n : skillNames) set.add(skill(n));
        j.setSkills(set);
        return j;
    }

    @Test
    @DisplayName("calculateMatch: combina profile + job e reproduz cenario do PDF (score 85)")
    void calculateMatchCenarioPdf() {
        CandidateProfile p = profile(1L, Seniority.JUNIOR, "4000", WorkMode.REMOTE,
            "Java", "Spring Boot", "SQL", "React");
        Job j = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot", "Docker", "SQL");

        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(p));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(j));

        MatchResult result = matchService.calculateMatch(1L, 1L);

        assertEquals(85, result.finalScore());
        assertEquals(75, result.skillsScore());
        assertEquals(1L, result.jobId());
    }

    @Test
    @DisplayName("calculateMatch: profile inexistente -> ProfileNotFoundException (nao chama jobRepository)")
    void calculateMatchProfileNaoExiste() {
        when(profileRepository.findByUser_Id(99L)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class,
            () -> matchService.calculateMatch(99L, 1L));
    }

    @Test
    @DisplayName("calculateMatch: profile existe mas job nao -> JobNotFoundException")
    void calculateMatchJobNaoExiste() {
        CandidateProfile p = profile(1L, Seniority.JUNIOR, "4000", WorkMode.REMOTE, "Java");
        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(p));
        when(jobRepository.findById(9999L)).thenReturn(Optional.empty());

        JobNotFoundException ex = assertThrows(JobNotFoundException.class,
            () -> matchService.calculateMatch(1L, 9999L));
        assertTrue(ex.getMessage().contains("9999"));
    }

    @Test
    @DisplayName("recommend: lista todas as vagas ordenadas por score desc")
    void recommendOrdenaDesc() {
        CandidateProfile p = profile(1L, Seniority.JUNIOR, "4000", WorkMode.REMOTE,
            "Java", "Spring Boot", "SQL", "React");

        // Vaga 1: match perfeito - score 100
        Job perfeita = job(1L, Seniority.JUNIOR, WorkMode.REMOTE, "3500", "5000",
            "Java", "Spring Boot", "SQL", "React");
        // Vaga 2: 0 skills, senioridade muito distante - score baixissimo
        Job ruim = job(2L, Seniority.SENIOR, WorkMode.ONSITE, "20000", "30000", "Python", "Django");
        // Vaga 3: meio termo
        Job media = job(3L, Seniority.JUNIOR, WorkMode.HYBRID, "3500", "5000", "Java", "Spring Boot");

        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(p));
        when(jobRepository.findAll()).thenReturn(List.of(ruim, media, perfeita));

        List<RecommendedJob> result = matchService.recommend(1L);

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).job().id(), "perfeita primeiro");
        assertEquals(100, result.get(0).finalScore());
        assertEquals(2L, result.get(2).job().id(), "ruim por ultimo");
        // Confere ordenacao monotonicamente decrescente
        assertTrue(result.get(0).finalScore() >= result.get(1).finalScore());
        assertTrue(result.get(1).finalScore() >= result.get(2).finalScore());
    }
}
