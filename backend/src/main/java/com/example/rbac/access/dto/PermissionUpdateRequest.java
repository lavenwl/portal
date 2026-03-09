package com.example.rbac.access.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PermissionUpdateRequest {

    @NotBlank(message = "code is required")
    @Size(max = 128, message = "code length must be <= 128")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 128, message = "name length must be <= 128")
    private String name;

    @Size(max = 32, message = "type length must be <= 32")
    private String type;

    @Size(max = 255, message = "resource length must be <= 255")
    private String resource;

    @Size(max = 16, message = "method length must be <= 16")
    private String method;

    @Size(max = 255, message = "description length must be <= 255")
    private String description;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
