package com.example.rbac.access;

import com.example.rbac.access.dto.BindRoleRequest;
import com.example.rbac.common.ApiResponse;
import com.example.rbac.log.aop.OperationLog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final AccessControlService accessControlService;

    public UserRoleController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('role:assign')")
    @OperationLog(module = "USER_ROLE", action = "BIND_ROLES")
    public ApiResponse<Void> bindRoles(@PathVariable Long id, @Valid @RequestBody BindRoleRequest request) {
        accessControlService.bindRolesToUser(id, request.getRoleIds());
        return ApiResponse.success();
    }
}
