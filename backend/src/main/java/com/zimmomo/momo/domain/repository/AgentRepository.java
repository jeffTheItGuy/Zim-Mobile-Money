package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {
    Optional<Agent> findByUserId(UUID userId);
    Optional<Agent> findByAgentCode(String agentCode);

    @Modifying
    @Query("UPDATE Agent a SET a.floatBalance = a.floatBalance + :amount WHERE a.agentId = :agentId")
    int addFloat(@Param("agentId") UUID agentId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Agent a SET a.floatBalance = a.floatBalance - :amount WHERE a.agentId = :agentId AND a.floatBalance >= :amount")
    int deductFloat(@Param("agentId") UUID agentId, @Param("amount") BigDecimal amount);
}
