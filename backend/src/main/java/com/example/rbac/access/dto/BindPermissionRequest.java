package com.example.rbac.access.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class BindPermissionRequest {

    @NotNull(message = "permissionIds is required")
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<Long> permissionIds) { this.permissionIds = permissionIds; }
}
