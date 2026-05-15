package com.devmatch.profile;

import com.devmatch.profile.dto.ProfileResponse;
import com.devmatch.profile.dto.UpdateProfileRequest;
import com.devmatch.skill.Skill;
import com.devmatch.skill.SkillRepository;
import com.devmatch.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private CandidateProfileRepository profileRepository;
    @Mock private SkillRepository skillRepository;

    @InjectMocks private ProfileService profileService;

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
        u.setPasswordHash("hashed");
        return u;
    }

    private static CandidateProfile emptyProfile(Long userId) {
        CandidateProfile p = new CandidateProfile();
        p.setId(10L);
        p.setUser(user(userId));
        p.setSkills(new HashSet<>());
        return p;
    }

    @Test
    @DisplayName("getMyProfile: profile existe -> retorna ProfileResponse")
    void getMyProfileExiste() {
        CandidateProfile profile = emptyProfile(1L);
        profile.setSeniority(Seniority.JUNIOR);
        profile.setDesiredSalary(new BigDecimal("4000"));
        profile.setPreferredWorkModes(new HashSet<>(Set.of(WorkMode.REMOTE)));
        profile.getSkills().add(skill("Java"));

        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getMyProfile(1L);

        assertEquals(10L, response.id());
        assertEquals(1L, response.userId());
        assertEquals(Seniority.JUNIOR, response.seniority());
        assertEquals(new BigDecimal("4000"), response.desiredSalary());
        assertEquals(Set.of(WorkMode.REMOTE), response.preferredWorkModes());
        assertEquals(1, response.skills().size());
        assertEquals("Java", response.skills().get(0).name());
    }

    @Test
    @DisplayName("getMyProfile: profile nao existe -> lanca ProfileNotFoundException")
    void getMyProfileNaoExiste() {
        when(profileRepository.findByUser_Id(99L)).thenReturn(Optional.empty());

        ProfileNotFoundException ex = assertThrows(
            ProfileNotFoundException.class,
            () -> profileService.getMyProfile(99L)
        );
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("updateMyProfile: skills validas -> atualiza campos e substitui skills")
    void updateMyProfileSkillsValidas() {
        CandidateProfile profile = emptyProfile(1L);
        Skill old = skill("Python");
        profile.getSkills().add(old);

        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByNameIn(anyCollection()))
            .thenReturn(List.of(skill("Java"), skill("Spring Boot")));

        UpdateProfileRequest req = new UpdateProfileRequest(
            Seniority.MID_LEVEL,
            new BigDecimal("8000"),
            Set.of(WorkMode.HYBRID, WorkMode.REMOTE),
            "Dev backend",
            List.of("Java", "Spring Boot")
        );

        ProfileResponse response = profileService.updateMyProfile(1L, req);

        assertEquals(Seniority.MID_LEVEL, response.seniority());
        assertEquals(new BigDecimal("8000"), response.desiredSalary());
        assertEquals(Set.of(WorkMode.HYBRID, WorkMode.REMOTE), response.preferredWorkModes());
        assertEquals("Dev backend", response.professionalSummary());
        assertEquals(2, response.skills().size(), "skills antigas substituidas");

        Set<String> names = profile.getSkills().stream()
            .map(Skill::getName)
            .collect(java.util.stream.Collectors.toSet());
        assertEquals(Set.of("Java", "Spring Boot"), names);
    }

    @Test
    @DisplayName("updateMyProfile: skill desconhecida -> lanca UnknownSkillException")
    void updateMyProfileSkillDesconhecida() {
        CandidateProfile profile = emptyProfile(1L);
        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
        when(skillRepository.findByNameIn(anyCollection()))
            .thenReturn(List.of(skill("Java")));

        UpdateProfileRequest req = new UpdateProfileRequest(
            Seniority.JUNIOR,
            new BigDecimal("4000"),
            Set.of(WorkMode.REMOTE),
            null,
            List.of("Java", "COBOL", "Visual Basic")
        );

        UnknownSkillException ex = assertThrows(
            UnknownSkillException.class,
            () -> profileService.updateMyProfile(1L, req)
        );
        assertEquals(Set.of("COBOL", "Visual Basic"), ex.getUnknownSkills());
    }

    @Test
    @DisplayName("updateMyProfile: lista vazia -> zera skills")
    void updateMyProfileSkillsVazia() {
        CandidateProfile profile = emptyProfile(1L);
        profile.getSkills().add(skill("Java"));
        profile.getSkills().add(skill("React"));

        when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));

        UpdateProfileRequest req = new UpdateProfileRequest(
            Seniority.SENIOR,
            new BigDecimal("12000"),
            Set.of(WorkMode.REMOTE),
            null,
            List.of()
        );

        ProfileResponse response = profileService.updateMyProfile(1L, req);

        assertEquals(0, response.skills().size());
        verify(skillRepository, never()).findByNameIn(anyCollection());
    }
}
