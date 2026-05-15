package com.devmatch.profile;

import com.devmatch.profile.dto.ProfileResponse;
import com.devmatch.profile.dto.UpdateProfileRequest;
import com.devmatch.skill.Skill;
import com.devmatch.skill.SkillRepository;
import com.devmatch.skill.dto.SkillResponse;
import com.devmatch.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CandidateProfileRepository profileRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ProfileNotFoundException(userId));
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        CandidateProfile profile = profileRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ProfileNotFoundException(userId));

        Set<Skill> resolved = resolveSkills(request.skillNames());

        profile.setSeniority(request.seniority());
        profile.setDesiredSalary(request.desiredSalary());
        profile.setProfessionalSummary(
            request.professionalSummary() == null ? null : request.professionalSummary().trim()
        );

        profile.getPreferredWorkModes().clear();
        profile.getPreferredWorkModes().addAll(request.preferredWorkModes());

        profile.getSkills().clear();
        profile.getSkills().addAll(resolved);

        return toResponse(profile);
    }

    private Set<Skill> resolveSkills(List<String> rawNames) {
        Set<String> normalized = rawNames.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toCollection(HashSet::new));

        if (normalized.isEmpty()) {
            return new HashSet<>();
        }

        List<Skill> found = skillRepository.findByNameIn(normalized);
        Set<String> foundNames = found.stream().map(Skill::getName).collect(Collectors.toSet());

        Set<String> unknown = normalized.stream()
            .filter(n -> !foundNames.contains(n))
            .collect(Collectors.toCollection(java.util.TreeSet::new));

        if (!unknown.isEmpty()) {
            throw new UnknownSkillException(unknown);
        }

        return new HashSet<>(found);
    }

    private ProfileResponse toResponse(CandidateProfile profile) {
        User user = profile.getUser();
        List<SkillResponse> skills = profile.getSkills().stream()
            .sorted(Comparator.comparing(Skill::getName))
            .map(s -> new SkillResponse(s.getId(), s.getName()))
            .toList();
        return new ProfileResponse(
            profile.getId(),
            user.getId(),
            user.getName(),
            user.getEmail(),
            profile.getSeniority(),
            profile.getDesiredSalary(),
            new java.util.HashSet<>(profile.getPreferredWorkModes()),
            profile.getProfessionalSummary(),
            skills
        );
    }
}
