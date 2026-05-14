package com.devmatch.auth;

import com.devmatch.auth.dto.AuthResponse;
import com.devmatch.auth.dto.LoginRequest;
import com.devmatch.auth.dto.RegisterRequest;
import com.devmatch.profile.CandidateProfile;
import com.devmatch.profile.CandidateProfileRepository;
import com.devmatch.user.Role;
import com.devmatch.user.User;
import com.devmatch.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CandidateProfileRepository profileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    @Test
    @DisplayName("register: email novo -> cria User + CandidateProfile vazio e retorna JWT")
    void registerNovoUsuario() {
        RegisterRequest req = new RegisterRequest("Rafael", "Rafael@Example.com", "senha12345");

        when(userRepository.existsByEmail("rafael@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha12345")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationHours()).thenReturn(24L);

        AuthResponse response = authService.register(req);

        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(24L, response.expiresInHours());
        assertEquals("rafael@example.com", response.user().email());
        assertEquals("Rafael", response.user().name());

        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCap.capture());
        assertEquals("rafael@example.com", userCap.getValue().getEmail(), "email normalizado para lowercase");
        assertEquals("hashed", userCap.getValue().getPasswordHash(), "senha foi hasheada");
        assertEquals(Role.CANDIDATE, userCap.getValue().getRole());

        ArgumentCaptor<CandidateProfile> profileCap = ArgumentCaptor.forClass(CandidateProfile.class);
        verify(profileRepository).save(profileCap.capture());
        assertEquals(1L, profileCap.getValue().getUser().getId(), "profile vinculado ao user salvo");
    }

    @Test
    @DisplayName("register: email duplicado -> lanca EmailAlreadyExistsException e nao salva nada")
    void registerEmailDuplicado() {
        RegisterRequest req = new RegisterRequest("Outro", "rafael@example.com", "senha12345");
        when(userRepository.existsByEmail("rafael@example.com")).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.register(req)
        );
        assertEquals("Email already registered: rafael@example.com", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("register: trim no nome e lowercase no email mesmo com espacos e caixa alta")
    void registerNormalizaEntradas() {
        RegisterRequest req = new RegisterRequest("  Rafael  ", "  RAFA@Example.COM  ", "senha12345");

        when(userRepository.existsByEmail("rafa@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha12345")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.getExpirationHours()).thenReturn(24L);

        AuthResponse response = authService.register(req);

        assertEquals("Rafael", response.user().name());
        assertEquals("rafa@example.com", response.user().email());
    }

    @Test
    @DisplayName("login: credenciais validas -> autentica via manager e retorna JWT")
    void loginValido() {
        LoginRequest req = new LoginRequest("Rafael@Example.com", "senha12345");

        User user = new User();
        user.setId(1L);
        user.setName("Rafael");
        user.setEmail("rafael@example.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.CANDIDATE);

        when(userRepository.findByEmail("rafael@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationHours()).thenReturn(24L);

        AuthResponse response = authService.login(req);

        assertEquals("jwt-token", response.token());
        assertEquals(1L, response.user().id());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCap =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCap.capture());
        assertEquals("rafael@example.com", authCap.getValue().getPrincipal(), "email normalizado");
        assertEquals("senha12345", authCap.getValue().getCredentials());
    }

    @Test
    @DisplayName("login: credenciais invalidas -> AuthenticationManager lanca e service propaga")
    void loginCredenciaisInvalidas() {
        LoginRequest req = new LoginRequest("rafael@example.com", "errada");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
        verify(jwtService, never()).generateToken(any());
    }
}
