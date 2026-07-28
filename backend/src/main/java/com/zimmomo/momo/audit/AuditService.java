package com.zimmomo.momo.audit;

import com.zimmomo.momo.domain.model.entity.AuditLog;
import com.zimmomo.momo.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async("auditExecutor")
    public void log(UUID userId, String action, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .details(details)
                .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }

    @Async("auditExecutor")
    public void log(UUID userId, String action, String details, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to write audit log: {}", e.getMessage());
        }
    }
}
