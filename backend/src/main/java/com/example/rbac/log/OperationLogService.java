package com.example.rbac.log;

import com.example.rbac.common.PageResponse;
import com.example.rbac.log.dto.OperationLogResponse;
import com.example.rbac.log.entity.OperationLogEntity;
import com.example.rbac.log.repository.OperationLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    public OperationLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Transactional
    public void save(OperationLogEntity entity) {
        operationLogRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public PageResponse<OperationLogResponse> list(int page, int size, String module) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<OperationLogEntity> result = (module == null || module.trim().isEmpty())
                ? operationLogRepository.findAll(pageable)
                : operationLogRepository.findByModuleContainingIgnoreCase(module.trim(), pageable);

        List<OperationLogResponse> records = result.getContent().stream().map(this::toResponse).collect(Collectors.toList());
        return new PageResponse<>(records, result.getTotalElements(), page, size);
    }

    private OperationLogResponse toResponse(OperationLogEntity entity) {
        OperationLogResponse response = new OperationLogResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setUsername(entity.getUsername());
        response.setModule(entity.getModule());
        response.setAction(entity.getAction());
        response.setRequestPath(entity.getRequestPath());
        response.setRequestMethod(entity.getRequestMethod());
        response.setRequestParams(entity.getRequestParams());
        response.setResponseCode(entity.getResponseCode());
        response.setSuccess(entity.getSuccess());
        response.setErrorMessage(entity.getErrorMessage());
        response.setDurationMs(entity.getDurationMs());
        response.setTraceId(entity.getTraceId());
        response.setOperatedAt(entity.getOperatedAt());
        return response;
    }
}
