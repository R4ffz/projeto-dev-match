package com.devmatch.job;

import com.devmatch.job.dto.JobFilter;
import com.devmatch.job.dto.JobResponse;
import com.devmatch.profile.Seniority;
import com.devmatch.profile.WorkMode;
import com.devmatch.skill.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock private JobRepository jobRepository;

    @InjectMocks private JobService jobService;

    private static final AtomicLong SKILL_ID = new AtomicLong(1);

    private static Skill skill(String name) {
        Skill s = new Skill(name);
        s.setId(SKILL_ID.incrementAndGet());
        return s;
    }

    private static Job job(Long id, String title, String... skillNames) {
        Job j = new Job();
        j.setId(id);
        j.setTitle(title);
        j.setCompany("Company " + id);
        j.setDescription("Description " + id);
        j.setSeniority(Seniority.JUNIOR);
        j.setWorkMode(WorkMode.REMOTE);
        j.setMinSalary(new BigDecimal("3500"));
        j.setMaxSalary(new BigDecimal("5000"));
        Set<Skill> set = new LinkedHashSet<>();
        for (String n : skillNames) set.add(skill(n));
        j.setSkills(set);
        return j;
    }

    @Test
    @DisplayName("search: retorna lista ordenada por id mapeada para JobResponse")
    void searchOrdenaPorId() {
        Job j1 = job(1L, "Vaga 1", "Java", "SQL");
        Job j2 = job(2L, "Vaga 2", "React");
        Job j3 = job(3L, "Vaga 3", "Docker");

        when(jobRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(j3, j1, j2));  // banco devolve fora de ordem

        List<JobResponse> result = jobService.search(new JobFilter(null, null, null, null, null));

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(JobResponse::id).toList(),
            "service ordena por id ascendente");
        assertEquals("Java", result.get(0).skills().get(0).name(),
            "skills tambem ordenadas alfabeticamente no DTO");
    }

    @Test
    @DisplayName("findById: vaga existe -> retorna JobResponse com description e skills")
    void findByIdExiste() {
        Job j = job(42L, "Backend Pleno", "Java", "Spring Boot", "PostgreSQL");
        when(jobRepository.findById(42L)).thenReturn(Optional.of(j));

        JobResponse response = jobService.findById(42L);

        assertEquals(42L, response.id());
        assertEquals("Backend Pleno", response.title());
        assertTrue(response.description().contains("Description"));
        assertEquals(3, response.skills().size());
    }

    @Test
    @DisplayName("findById: vaga nao existe -> lanca JobNotFoundException")
    void findByIdNaoExiste() {
        when(jobRepository.findById(9999L)).thenReturn(Optional.empty());

        JobNotFoundException ex = assertThrows(
            JobNotFoundException.class,
            () -> jobService.findById(9999L)
        );
        assertTrue(ex.getMessage().contains("9999"));
    }
}
