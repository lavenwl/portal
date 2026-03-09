package com.example.rbac.access.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RoleCreateRequest {

    @NotBlank(message = "code is required")
    @Size(max = 64, message = "code length must be <= 64")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 64, message = "name length must be <= 64")
    private String name;

    @Size(max = 255, message = "description length must be <= 255")
    private String description;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
