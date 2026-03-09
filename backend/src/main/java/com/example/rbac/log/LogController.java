package com.example.rbac.log;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.common.PageResponse;
import com.example.rbac.log.dto.LoginLogResponse;
import com.example.rbac.log.dto.OperationLogResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LogController {

    private final LoginLogService loginLogService;
    private final OperationLogService operationLogService;

    public LogController(LoginLogService loginLogService, OperationLogService operationLogService) {
        this.loginLogService = loginLogService;
        this.operationLogService = operationLogService;
    }

    @GetMapping("/api/logins")
    @PreAuthorize("hasAuthority('loginlog:read')")
    public ApiResponse<PageResponse<LoginLogResponse>> listLoginLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username
    ) {
        return ApiResponse.success(loginLogService.list(page, size, username));
    }

    @GetMapping("/api/operations")
    @PreAuthorize("hasAuthority('operationlog:read')")
    public ApiResponse<PageResponse<OperationLogResponse>> listOperationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module
    ) {
        return ApiResponse.success(operationLogService.list(page, size, module));
    }
}
