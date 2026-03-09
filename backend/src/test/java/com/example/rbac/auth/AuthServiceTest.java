package com.example.rbac.auth;

import com.example.rbac.access.AccessControlService;
import com.example.rbac.auth.dto.LoginRequest;
import com.example.rbac.auth.dto.LoginResponse;
import com.example.rbac.auth.dto.RefreshTokenRequest;
import com.example.rbac.auth.dto.RegisterRequest;
import com.example.rbac.auth.entity.RefreshTokenEntity;
import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.RefreshTokenRepository;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.auth.token.JwtTokenService;
import com.example.rbac.exception.BusinessException;
import com.example.rbac.log.LoginLogService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private AccessControlService accessControlService;
    @Mock
    private LoginLogService loginLogService;

    @InjectMocks
    private AuthService authService;

    private UserEntity activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new UserEntity();
        activeUser.setId(1L);
        activeUser.setUsername("alice");
        activeUser.setEmail("alice@test.com");
        activeUser.setPasswordHash("hashed");
        activeUser.setStatus(1);
        activeUser.setDeleted(0);
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@test.com");
        request.setPassword("123456");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));
        assertEquals(40002, ex.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnTokensAndPersistRefreshToken() {
        LoginRequest request = new LoginRequest();
        request.setAccount("alice");
        request.setPassword("123456");

        when(userRepository.findByUsernameOrEmail("alice", "alice")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("123456", "hashed")).thenReturn(true);
        when(accessControlService.resolveAuthoritiesByUserId(1L)).thenReturn(List.of("user:read"));
        when(jwtTokenService.createAccessToken(1L, "alice", List.of("user:read"))).thenReturn("access-token");
        when(jwtTokenService.createRefreshToken(1L, "alice")).thenReturn("refresh-token");
        when(jwtTokenService.getAccessExpireSeconds()).thenReturn(3600L);
        when(jwtTokenService.getRefreshExpireSeconds()).thenReturn(7200L);

        LoginResponse response = authService.login(request, "127.0.0.1", "JUnit");

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(3600L, response.getExpiresIn());

        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getUserId());
        assertEquals("refresh-token", captor.getValue().getToken());
        assertEquals(0, captor.getValue().getRevoked());
        verify(loginLogService).recordLogin(1L, "alice", true, "success", "127.0.0.1", "JUnit");
    }

    @Test
    void login_shouldRecordFailureWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setAccount("ghost");
        request.setPassword("123456");

        when(userRepository.findByUsernameOrEmail("ghost", "ghost")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request, "10.0.0.1", "UA"));
        assertEquals(40003, ex.getCode());
        verify(loginLogService).recordLogin(null, "ghost", false, "account or password is incorrect", "10.0.0.1", "UA");
    }

    @Test
    void refresh_shouldThrowWhenTokenTypeIsNotRefresh() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("bad-token");

        Claims claims = Jwts.claims();
        claims.setSubject("1");
        claims.put("tokenType", "access");
        when(jwtTokenService.parseToken("bad-token")).thenReturn(claims);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refresh(request));
        assertEquals(40004, ex.getCode());
        verify(refreshTokenRepository, never()).findByTokenAndRevoked(anyString(), anyInt());
    }

    @Test
    void refresh_shouldRotateRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh");

        Claims claims = Jwts.claims();
        claims.setSubject("1");
        claims.put("tokenType", "refresh");

        RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
        tokenEntity.setUserId(1L);
        tokenEntity.setToken("old-refresh");
        tokenEntity.setRevoked(0);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(jwtTokenService.parseToken("old-refresh")).thenReturn(claims);
        when(refreshTokenRepository.findByTokenAndRevoked("old-refresh", 0)).thenReturn(Optional.of(tokenEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(accessControlService.resolveAuthoritiesByUserId(1L)).thenReturn(List.of("user:read"));
        when(jwtTokenService.createAccessToken(1L, "alice", List.of("user:read"))).thenReturn("new-access");
        when(jwtTokenService.createRefreshToken(1L, "alice")).thenReturn("new-refresh");
        when(jwtTokenService.getAccessExpireSeconds()).thenReturn(3600L);
        when(jwtTokenService.getRefreshExpireSeconds()).thenReturn(7200L);

        LoginResponse response = authService.refresh(request);

        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
        verify(refreshTokenRepository, times(2)).save(any(RefreshTokenEntity.class));
    }
}
