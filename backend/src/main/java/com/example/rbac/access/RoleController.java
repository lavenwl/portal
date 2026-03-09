package com.example.rbac.access;

import com.example.rbac.access.dto.BindPermissionRequest;
import com.example.rbac.access.dto.RoleCreateRequest;
import com.example.rbac.access.dto.RoleResponse;
import com.example.rbac.access.dto.RoleUpdateRequest;
import com.example.rbac.common.ApiResponse;
import com.example.rbac.log.aop.OperationLog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final AccessControlService accessControlService;

    public RoleController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ApiResponse<List<RoleResponse>> listRoles() {
        return ApiResponse.success(accessControlService.listRoles());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    @OperationLog(module = "ROLE", action = "CREATE")
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        return ApiResponse.success(accessControlService.createRole(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    @OperationLog(module = "ROLE", action = "UPDATE")
    public ApiResponse<RoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return ApiResponse.success(accessControlService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @OperationLog(module = "ROLE", action = "DELETE")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        accessControlService.deleteRole(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    @OperationLog(module = "ROLE", action = "BIND_PERMISSIONS")
    public ApiResponse<Void> bindPermissions(@PathVariable Long id, @Valid @RequestBody BindPermissionRequest request) {
        accessControlService.bindPermissions(id, request.getPermissionIds());
        return ApiResponse.success();
    }
}
