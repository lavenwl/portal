package com.example.rbac.user;

import com.example.rbac.common.PageResponse;
import com.example.rbac.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void listUsers_shouldReturnUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(authorities = {"user:read"})
    void listUsers_shouldReturnOkWithAuthority() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setUsername("alice");

        when(userService.listUsers(anyInt(), anyInt(), any())).thenReturn(new PageResponse<>(List.of(user), 1, 0, 10));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].username").value("alice"));
    }

    @Test
    @WithMockUser(authorities = {"permission:read"})
    void listUsers_shouldReturnForbiddenWhenAuthorityMissing() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}
