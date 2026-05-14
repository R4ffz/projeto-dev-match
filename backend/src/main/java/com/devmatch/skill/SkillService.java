package com.devmatch.skill;

import com.devmatch.skill.dto.SkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<SkillResponse> listAll() {
        return skillRepository.findAll().stream()
            .sorted(Comparator.comparing(Skill::getName))
            .map(s -> new SkillResponse(s.getId(), s.getName()))
            .toList();
    }
}
