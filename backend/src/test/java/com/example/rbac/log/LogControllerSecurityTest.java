package com.example.rbac.log;

import com.example.rbac.common.PageResponse;
import com.example.rbac.log.dto.LoginLogResponse;
import com.example.rbac.log.dto.OperationLogResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LogControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginLogService loginLogService;

    @MockBean
    private OperationLogService operationLogService;

    @Test
    @WithMockUser(authorities = {"loginlog:read"})
    void listLoginLogs_shouldReturnOkWithAuthority() throws Exception {
        LoginLogResponse item = new LoginLogResponse();
        item.setUsername("alice");
        when(loginLogService.list(anyInt(), anyInt(), any())).thenReturn(new PageResponse<>(List.of(item), 1, 0, 10));

        mockMvc.perform(get("/api/logins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].username").value("alice"));
    }

    @Test
    @WithMockUser(authorities = {"operationlog:read"})
    void listOperationLogs_shouldReturnOkWithAuthority() throws Exception {
        OperationLogResponse item = new OperationLogResponse();
        item.setModule("USER");
        when(operationLogService.list(anyInt(), anyInt(), any())).thenReturn(new PageResponse<>(List.of(item), 1, 0, 10));

        mockMvc.perform(get("/api/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[0].module").value("USER"));
    }

    @Test
    @WithMockUser(authorities = {"user:read"})
    void listLoginLogs_shouldReturnForbiddenWithoutAuthority() throws Exception {
        mockMvc.perform(get("/api/logins"))
                .andExpect(status().isForbidden());
    }
}
