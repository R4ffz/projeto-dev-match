package com.devmatch.auth;

import com.devmatch.auth.dto.AuthResponse;
import com.devmatch.auth.dto.LoginRequest;
import com.devmatch.auth.dto.RegisterRequest;
import com.devmatch.auth.dto.UserSummary;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.CandidateProfileRepository;
import com.devmatch.user.Role;
import com.devmatch.user.User;
import com.devmatch.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.CANDIDATE);
        user = userRepository.save(user);

        CandidateProfile profile = new CandidateProfile();
        profile.setUser(user);
        profileRepository.save(profile);

        return AuthResponse.bearer(
            jwtService.generateToken(user),
            jwtService.getExpirationHours(),
            new UserSummary(user.getId(), user.getName(), user.getEmail())
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.password())
        );
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
        return AuthResponse.bearer(
            jwtService.generateToken(user),
            jwtService.getExpirationHours(),
            new UserSummary(user.getId(), user.getName(), user.getEmail())
        );
    }
}
