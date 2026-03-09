package com.example.rbac.access;

import com.example.rbac.access.dto.*;
import com.example.rbac.access.entity.PermissionEntity;
import com.example.rbac.access.entity.RoleEntity;
import com.example.rbac.access.entity.RolePermissionEntity;
import com.example.rbac.access.entity.UserRoleEntity;
import com.example.rbac.access.repository.*;
import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AuthorityQueryRepository authorityQueryRepository;
    private final UserRepository userRepository;

    public AccessControlService(RoleRepository roleRepository,
                                PermissionRepository permissionRepository,
                                UserRoleRepository userRoleRepository,
                                RolePermissionRepository rolePermissionRepository,
                                AuthorityQueryRepository authorityQueryRepository,
                                UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.authorityQueryRepository = authorityQueryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<String> resolveAuthoritiesByUserId(Long userId) {
        List<String> authorities = authorityQueryRepository.findPermissionCodesByUserId(userId);
        return authorities == null ? Collections.emptyList() : authorities;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream().map(this::toRoleResponse).collect(Collectors.toList());
    }

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        if (roleRepository.existsByCode(request.getCode())) {
            throw new BusinessException(40020, "role code already exists");
        }
        RoleEntity role = new RoleEntity();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setStatus(1);
        roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        RoleEntity role = roleRepository.findById(id).orElseThrow(() -> new BusinessException(40021, "role not found"));
        if (roleRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new BusinessException(40020, "role code already exists");
        }
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        RoleEntity role = roleRepository.findById(id).orElseThrow(() -> new BusinessException(40021, "role not found"));
        rolePermissionRepository.deleteByRoleId(role.getId());
        roleRepository.delete(role);
    }

    @Transactional
    public void bindPermissions(Long roleId, List<Long> permissionIds) {
        roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(40021, "role not found"));
        rolePermissionRepository.deleteByRoleId(roleId);

        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        List<PermissionEntity> permissionEntities = permissionRepository.findByIdIn(permissionIds);
        if (permissionEntities.size() != new HashSet<>(permissionIds).size()) {
            throw new BusinessException(40022, "some permissions do not exist");
        }

        List<RolePermissionEntity> bindings = new ArrayList<>();
        for (Long permissionId : new HashSet<>(permissionIds)) {
            RolePermissionEntity entity = new RolePermissionEntity();
            entity.setRoleId(roleId);
            entity.setPermissionId(permissionId);
            bindings.add(entity);
        }
        rolePermissionRepository.saveAll(bindings);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream().map(this::toPermissionResponse).collect(Collectors.toList());
    }

    @Transactional
    public PermissionResponse createPermission(PermissionCreateRequest request) {
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new BusinessException(40023, "permission code already exists");
        }

        PermissionEntity entity = new PermissionEntity();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setType(request.getType() == null || request.getType().trim().isEmpty() ? "API" : request.getType());
        entity.setResource(request.getResource());
        entity.setMethod(request.getMethod());
        entity.setDescription(request.getDescription());
        permissionRepository.save(entity);

        return toPermissionResponse(entity);
    }

    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionUpdateRequest request) {
        PermissionEntity entity = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40024, "permission not found"));
        if (permissionRepository.existsByCodeAndIdNot(request.getCode(), id)) {
            throw new BusinessException(40023, "permission code already exists");
        }

        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setType(request.getType() == null || request.getType().trim().isEmpty() ? "API" : request.getType());
        entity.setResource(request.getResource());
        entity.setMethod(request.getMethod());
        entity.setDescription(request.getDescription());
        permissionRepository.save(entity);

        return toPermissionResponse(entity);
    }

    @Transactional
    public void deletePermission(Long id) {
        PermissionEntity entity = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40024, "permission not found"));
        permissionRepository.delete(entity);
    }

    @Transactional
    public void bindRolesToUser(Long userId, List<Long> roleIds) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(40007, "user not found"));
        if (Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException(40007, "user not found");
        }

        userRoleRepository.deleteByUserId(userId);

        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }

        Set<Long> uniqueRoleIds = new HashSet<>(roleIds);
        List<RoleEntity> roles = roleRepository.findAllById(uniqueRoleIds);
        if (roles.size() != uniqueRoleIds.size()) {
            throw new BusinessException(40021, "some roles do not exist");
        }

        List<UserRoleEntity> bindings = new ArrayList<>();
        for (Long roleId : uniqueRoleIds) {
            UserRoleEntity entity = new UserRoleEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            bindings.add(entity);
        }
        userRoleRepository.saveAll(bindings);
    }

    private RoleResponse toRoleResponse(RoleEntity role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setCode(role.getCode());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setStatus(role.getStatus());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        return response;
    }

    private PermissionResponse toPermissionResponse(PermissionEntity entity) {
        PermissionResponse response = new PermissionResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setResource(entity.getResource());
        response.setMethod(entity.getMethod());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
