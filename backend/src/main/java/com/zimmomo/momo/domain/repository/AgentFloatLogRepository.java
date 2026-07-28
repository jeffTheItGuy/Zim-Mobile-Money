package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.AgentFloatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentFloatLogRepository extends JpaRepository<AgentFloatLog, UUID> {
    List<AgentFloatLog> findByAgentIdOrderByCreatedAtDesc(UUID agentId);
}
