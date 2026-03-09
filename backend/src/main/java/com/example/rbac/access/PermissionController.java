package com.example.rbac.access;

import com.example.rbac.access.dto.PermissionCreateRequest;
import com.example.rbac.access.dto.PermissionResponse;
import com.example.rbac.access.dto.PermissionUpdateRequest;
import com.example.rbac.common.ApiResponse;
import com.example.rbac.log.aop.OperationLog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final AccessControlService accessControlService;

    public PermissionController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ApiResponse<List<PermissionResponse>> listPermissions() {
        return ApiResponse.success(accessControlService.listPermissions());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    @OperationLog(module = "PERMISSION", action = "CREATE")
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(accessControlService.createPermission(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    @OperationLog(module = "PERMISSION", action = "UPDATE")
    public ApiResponse<PermissionResponse> updatePermission(@PathVariable Long id,
                                                            @Valid @RequestBody PermissionUpdateRequest request) {
        return ApiResponse.success(accessControlService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    @OperationLog(module = "PERMISSION", action = "DELETE")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        accessControlService.deletePermission(id);
        return ApiResponse.success();
    }
}
