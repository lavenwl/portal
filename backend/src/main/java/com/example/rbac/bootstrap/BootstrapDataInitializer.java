package com.example.rbac.bootstrap;

import com.example.rbac.access.entity.PermissionEntity;
import com.example.rbac.access.entity.RoleEntity;
import com.example.rbac.access.entity.RolePermissionEntity;
import com.example.rbac.access.entity.UserRoleEntity;
import com.example.rbac.access.repository.PermissionRepository;
import com.example.rbac.access.repository.RolePermissionRepository;
import com.example.rbac.access.repository.RoleRepository;
import com.example.rbac.access.repository.UserRoleRepository;
import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class BootstrapDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.enabled:true}")
    private boolean enabled;

    @Value("${app.bootstrap.admin-username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin-email:admin@example.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password:Admin@123456}")
    private String adminPassword;

    public BootstrapDataInitializer(UserRepository userRepository,
                                    RoleRepository roleRepository,
                                    PermissionRepository permissionRepository,
                                    UserRoleRepository userRoleRepository,
                                    RolePermissionRepository rolePermissionRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            return;
        }

        RoleEntity adminRole = roleRepository.findByCode("SUPER_ADMIN").orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setCode("SUPER_ADMIN");
            role.setName("Super Admin");
            role.setDescription("Default super administrator role");
            role.setStatus(1);
            return roleRepository.save(role);
        });

        Map<String, String> seedPermissions = new LinkedHashMap<>();
        seedPermissions.put("user:read", "Read users");
        seedPermissions.put("user:create", "Create users");
        seedPermissions.put("user:update", "Update users");
        seedPermissions.put("user:delete", "Delete users");
        seedPermissions.put("role:read", "Read roles");
        seedPermissions.put("role:create", "Create roles");
        seedPermissions.put("role:update", "Update roles");
        seedPermissions.put("role:delete", "Delete roles");
        seedPermissions.put("role:assign", "Assign roles and permissions");
        seedPermissions.put("permission:read", "Read permissions");
        seedPermissions.put("permission:create", "Create permissions");
        seedPermissions.put("permission:update", "Update permissions");
        seedPermissions.put("permission:delete", "Delete permissions");
        seedPermissions.put("loginlog:read", "Read login logs");
        seedPermissions.put("operationlog:read", "Read operation logs");

        for (Map.Entry<String, String> entry : seedPermissions.entrySet()) {
            PermissionEntity permission = permissionRepository.findByCode(entry.getKey()).orElseGet(() -> {
                PermissionEntity entity = new PermissionEntity();
                entity.setCode(entry.getKey());
                entity.setName(entry.getValue());
                entity.setType("API");
                entity.setDescription("Bootstrap permission");
                return permissionRepository.save(entity);
            });

            if (rolePermissionRepository.findByRoleIdAndPermissionId(adminRole.getId(), permission.getId()).isEmpty()) {
                RolePermissionEntity binding = new RolePermissionEntity();
                binding.setRoleId(adminRole.getId());
                binding.setPermissionId(permission.getId());
                rolePermissionRepository.save(binding);
            }
        }

        UserEntity adminUser = userRepository.findByUsername(adminUsername).orElseGet(() -> {
            UserEntity user = new UserEntity();
            user.setUsername(adminUsername);
            user.setEmail(adminEmail);
            user.setPasswordHash(passwordEncoder.encode(adminPassword));
            user.setNickname("Administrator");
            user.setStatus(1);
            user.setDeleted(0);
            return userRepository.save(user);
        });

        if (userRoleRepository.findByUserIdAndRoleId(adminUser.getId(), adminRole.getId()).isEmpty()) {
            UserRoleEntity relation = new UserRoleEntity();
            relation.setUserId(adminUser.getId());
            relation.setRoleId(adminRole.getId());
            userRoleRepository.save(relation);
        }
    }
}
