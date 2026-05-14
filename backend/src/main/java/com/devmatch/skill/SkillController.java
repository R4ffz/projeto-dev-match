package com.devmatch.skill;

import com.devmatch.skill.dto.SkillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Catalogo de tecnologias disponiveis")
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Lista skills do catalogo ordenadas por nome")
    public List<SkillResponse> list() {
        return skillService.listAll();
    }
}
