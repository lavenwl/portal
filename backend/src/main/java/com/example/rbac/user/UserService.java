package com.example.rbac.user;

import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.exception.BusinessException;
import com.example.rbac.user.dto.CreateUserRequest;
import com.example.rbac.user.dto.UpdateUserRequest;
import com.example.rbac.user.dto.UserResponse;
import com.example.rbac.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(int page, int size, String keyword) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserEntity> result;
        if (keyword == null || keyword.trim().isEmpty()) {
            result = userRepository.findByDeleted(0, pageable);
        } else {
            String cleaned = keyword.trim();
            result = userRepository.findByDeletedAndUsernameContainingIgnoreCaseOrDeletedAndEmailContainingIgnoreCase(
                    0, cleaned, 0, cleaned, pageable
            );
        }

        List<UserResponse> records = result.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        UserEntity user = findActiveUser(id);
        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(40002, "username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(40005, "email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        user.setDeleted(0);

        userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        UserEntity user = findActiveUser(id);
        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BusinessException(40005, "email already exists");
        }

        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());

        userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        UserEntity user = findActiveUser(id);
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = findActiveUser(id);
        user.setDeleted(1);
        userRepository.save(user);
    }

    private UserEntity findActiveUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(40007, "user not found"));
        if (Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException(40007, "user not found");
        }
        return user;
    }

    private UserResponse toResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
