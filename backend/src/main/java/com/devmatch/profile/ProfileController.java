package com.devmatch.profile;

import com.devmatch.auth.AuthenticatedUser;
import com.devmatch.profile.dto.ProfileResponse;
import com.devmatch.profile.dto.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Perfil do candidato autenticado")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Retorna o perfil do candidato autenticado")
    public ProfileResponse getMyProfile(@AuthenticationPrincipal AuthenticatedUser principal) {
        return profileService.getMyProfile(principal.id());
    }

    @PutMapping("/me")
    @Operation(summary = "Atualiza o perfil do candidato autenticado")
    public ProfileResponse updateMyProfile(@AuthenticationPrincipal AuthenticatedUser principal,
                                            @Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateMyProfile(principal.id(), request);
    }
}
