package com.example.rbac.access;

import com.example.rbac.access.dto.BindPermissionRequest;
import com.example.rbac.access.dto.PermissionCreateRequest;
import com.example.rbac.access.dto.RoleCreateRequest;
import com.example.rbac.access.entity.PermissionEntity;
import com.example.rbac.access.entity.RoleEntity;
import com.example.rbac.access.repository.*;
import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RolePermissionRepository rolePermissionRepository;
    @Mock
    private AuthorityQueryRepository authorityQueryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccessControlService accessControlService;

    private RoleEntity role;
    private PermissionEntity permission;

    @BeforeEach
    void setUp() {
        role = new RoleEntity();
        role.setId(10L);
        role.setCode("ADMIN");
        role.setName("Admin");

        permission = new PermissionEntity();
        permission.setId(100L);
        permission.setCode("user:read");
        permission.setName("Read User");
    }

    @Test
    void resolveAuthoritiesByUserId_shouldReturnEmptyWhenNoBinding() {
        when(authorityQueryRepository.findPermissionCodesByUserId(1L)).thenReturn(Collections.emptyList());

        List<String> authorities = accessControlService.resolveAuthoritiesByUserId(1L);

        assertTrue(authorities.isEmpty());
    }

    @Test
    void createRole_shouldThrowWhenCodeExists() {
        RoleCreateRequest request = new RoleCreateRequest();
        request.setCode("ADMIN");
        request.setName("Admin");
        when(roleRepository.existsByCode("ADMIN")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> accessControlService.createRole(request));
        assertEquals(40020, ex.getCode());
    }

    @Test
    void bindPermissions_shouldThrowWhenPermissionMissing() {
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role));
        when(permissionRepository.findByIdIn(List.of(100L, 101L))).thenReturn(List.of(permission));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accessControlService.bindPermissions(10L, List.of(100L, 101L)));
        assertEquals(40022, ex.getCode());
    }

    @Test
    void bindRolesToUser_shouldCreateBindings() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setDeleted(0);

        RoleEntity role2 = new RoleEntity();
        role2.setId(11L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(any())).thenReturn(List.of(role, role2));

        accessControlService.bindRolesToUser(1L, List.of(10L, 11L));

        verify(userRoleRepository).deleteByUserId(1L);
        verify(userRoleRepository).saveAll(any());
    }
}
