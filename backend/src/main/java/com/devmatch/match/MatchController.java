package com.devmatch.match;

import com.devmatch.auth.AuthenticatedUser;
import com.devmatch.match.dto.MatchResult;
import com.devmatch.match.dto.RecommendedJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Match", description = "Recomendacoes e calculo de compatibilidade")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/recommended")
    @Operation(summary = "Lista todas as vagas ordenadas pelo score de match do candidato autenticado")
    public List<RecommendedJob> recommended(@AuthenticationPrincipal AuthenticatedUser principal) {
        return matchService.recommend(principal.id());
    }

    @GetMapping("/{id}/match")
    @Operation(summary = "Calcula o match detalhado entre o candidato autenticado e a vaga informada")
    public MatchResult match(@AuthenticationPrincipal AuthenticatedUser principal,
                              @PathVariable Long id) {
        return matchService.calculateMatch(principal.id(), id);
    }
}
