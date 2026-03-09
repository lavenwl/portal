package com.example.rbac.access.dto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class BindRoleRequest {

    @NotNull(message = "roleIds is required")
    private List<Long> roleIds;

    public List<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }
}
