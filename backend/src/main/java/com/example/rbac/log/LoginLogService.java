package com.example.rbac.log;

import com.example.rbac.common.PageResponse;
import com.example.rbac.log.dto.LoginLogResponse;
import com.example.rbac.log.entity.LoginLogEntity;
import com.example.rbac.log.repository.LoginLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @Transactional
    public void recordLogin(Long userId, String username, boolean success, String reason, String ip, String userAgent) {
        LoginLogEntity log = new LoginLogEntity();
        log.setUserId(userId);
        log.setUsername(username);
        log.setEventType("LOGIN");
        log.setSuccess(success ? 1 : 0);
        log.setReason(reason);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        loginLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoginLogResponse> list(int page, int size, String username) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<LoginLogEntity> result = (username == null || username.trim().isEmpty())
                ? loginLogRepository.findAll(pageable)
                : loginLogRepository.findByUsernameContainingIgnoreCase(username.trim(), pageable);

        List<LoginLogResponse> records = result.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotalElements(), page, size);
    }

    private LoginLogResponse toResponse(LoginLogEntity log) {
        LoginLogResponse response = new LoginLogResponse();
        response.setId(log.getId());
        response.setUserId(log.getUserId());
        response.setUsername(log.getUsername());
        response.setEventType(log.getEventType());
        response.setSuccess(log.getSuccess());
        response.setReason(log.getReason());
        response.setIp(log.getIp());
        response.setUserAgent(log.getUserAgent());
        response.setLoginAt(log.getLoginAt());
        return response;
    }
}
