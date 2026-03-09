package com.example.rbac.auth;

import com.example.rbac.auth.dto.LoginRequest;
import com.example.rbac.auth.dto.LoginResponse;
import com.example.rbac.auth.dto.RefreshTokenRequest;
import com.example.rbac.auth.dto.RegisterRequest;
import com.example.rbac.auth.entity.RefreshTokenEntity;
import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.RefreshTokenRepository;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.auth.token.JwtTokenService;
import com.example.rbac.access.AccessControlService;
import com.example.rbac.exception.BusinessException;
import com.example.rbac.log.LoginLogService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AccessControlService accessControlService;
    private final LoginLogService loginLogService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService,
                       AccessControlService accessControlService,
                       LoginLogService loginLogService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.accessControlService = accessControlService;
        this.loginLogService = loginLogService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(40002, "username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(40005, "email already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setEmail(request.getEmail());
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userEntity.setStatus(1);
        userEntity.setDeleted(0);

        userRepository.save(userEntity);
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String ip, String userAgent) {
        UserEntity user = userRepository.findByUsernameOrEmail(request.getAccount(), request.getAccount()).orElse(null);
        if (user == null) {
            loginLogService.recordLogin(null, request.getAccount(), false, "account or password is incorrect", ip, userAgent);
            throw new BusinessException(40003, "account or password is incorrect");
        }

        if (!Integer.valueOf(0).equals(user.getDeleted()) || !Integer.valueOf(1).equals(user.getStatus())) {
            loginLogService.recordLogin(user.getId(), user.getUsername(), false, "account is disabled", ip, userAgent);
            throw new BusinessException(40006, "account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginLogService.recordLogin(user.getId(), user.getUsername(), false, "account or password is incorrect", ip, userAgent);
            throw new BusinessException(40003, "account or password is incorrect");
        }

        List<String> authorities = accessControlService.resolveAuthoritiesByUserId(user.getId());
        String accessToken = jwtTokenService.createAccessToken(user.getId(), user.getUsername(), authorities);
        String refreshToken = jwtTokenService.createRefreshToken(user.getId(), user.getUsername());
        saveRefreshToken(user.getId(), refreshToken);
        loginLogService.recordLogin(user.getId(), user.getUsername(), true, "success", ip, userAgent);

        return new LoginResponse(accessToken, refreshToken, jwtTokenService.getAccessExpireSeconds());
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        Claims claims = parseTokenOrThrow(request.getRefreshToken(), "refresh token is invalid");
        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new BusinessException(40004, "refresh token is invalid");
        }

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByTokenAndRevoked(request.getRefreshToken(), 0)
                .orElseThrow(() -> new BusinessException(40004, "refresh token is invalid"));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenEntity.setRevoked(1);
            refreshTokenRepository.save(tokenEntity);
            throw new BusinessException(40004, "refresh token is invalid");
        }

        Long userId = Long.valueOf(claims.getSubject());
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(40007, "user not found"));

        tokenEntity.setRevoked(1);
        refreshTokenRepository.save(tokenEntity);

        List<String> authorities = accessControlService.resolveAuthoritiesByUserId(user.getId());
        String accessToken = jwtTokenService.createAccessToken(user.getId(), user.getUsername(), authorities);
        String refreshToken = jwtTokenService.createRefreshToken(user.getId(), user.getUsername());
        saveRefreshToken(user.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken, jwtTokenService.getAccessExpireSeconds());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenAndRevoked(request.getRefreshToken(), 0)
                .ifPresent(tokenEntity -> {
                    tokenEntity.setRevoked(1);
                    refreshTokenRepository.save(tokenEntity);
                });
    }

    private Claims parseTokenOrThrow(String token, String message) {
        try {
            return jwtTokenService.parseToken(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(40004, message);
        }
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUserId(userId);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setRevoked(0);
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenService.getRefreshExpireSeconds()));
        refreshTokenRepository.save(refreshTokenEntity);
    }
}
