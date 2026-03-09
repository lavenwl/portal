package com.example.rbac.user;

import com.example.rbac.auth.entity.UserEntity;
import com.example.rbac.auth.repository.UserRepository;
import com.example.rbac.exception.BusinessException;
import com.example.rbac.user.dto.CreateUserRequest;
import com.example.rbac.user.dto.UpdateUserRequest;
import com.example.rbac.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("bob");
        user.setEmail("bob@test.com");
        user.setPasswordHash("hashed");
        user.setStatus(1);
        user.setDeleted(0);
    }

    @Test
    void createUser_shouldThrowWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("new-user");
        request.setEmail("dup@test.com");
        request.setPassword("123456");

        when(userRepository.existsByUsername("new-user")).thenReturn(false);
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(request));
        assertEquals(40005, ex.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldEncodePasswordAndReturnResponse() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("new-user");
        request.setEmail("new@test.com");
        request.setPassword("123456");
        request.setNickname("Neo");

        when(userRepository.existsByUsername("new-user")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");

        UserResponse response = userService.createUser(request);

        assertEquals("new-user", response.getUsername());
        assertEquals("new@test.com", response.getEmail());
        assertEquals("Neo", response.getNickname());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUser_shouldThrowWhenEmailUsedByAnotherUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("used@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("used@test.com", 1L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.updateUser(1L, request));
        assertEquals(40005, ex.getCode());
    }

    @Test
    void deleteUser_shouldSetDeletedFlag() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertEquals(1, user.getDeleted());
        verify(userRepository).save(user);
    }
}
