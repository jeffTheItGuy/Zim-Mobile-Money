package com.zimmomo.momo.domain.service;

import com.zimmomo.momo.api.dto.response.AgentResponse;
import com.zimmomo.momo.domain.model.entity.Agent;
import com.zimmomo.momo.domain.model.entity.User;
import com.zimmomo.momo.domain.model.enums.AgentStatus;
import com.zimmomo.momo.domain.model.enums.UserType;
import com.zimmomo.momo.domain.repository.AgentRepository;
import com.zimmomo.momo.domain.repository.UserRepository;
import com.zimmomo.momo.exception.BusinessException;
import com.zimmomo.momo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AgentResponse onboardAgent(UUID userId, String agentCode, String businessName, String territory) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getUserType() != UserType.CUSTOMER) {
            throw new BusinessException("User must be a customer to become an agent");
        }

        if (agentRepository.findByAgentCode(agentCode).isPresent()) {
            throw new BusinessException("Agent code already exists");
        }

        user.setUserType(UserType.AGENT);
        userRepository.save(user);

        Agent agent = Agent.builder()
            .userId(userId)
            .agentCode(agentCode)
            .businessName(businessName)
            .territory(territory)
            .status(AgentStatus.ACTIVE)
            .build();

        agent = agentRepository.save(agent);
        return mapToResponse(agent);
    }

    @Transactional(readOnly = true)
    public AgentResponse getAgent(UUID agentId) {
        Agent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        return mapToResponse(agent);
    }

    @Transactional(readOnly = true)
    public Agent getAgentByUserId(UUID userId) {
        return agentRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
    }

    @Transactional(readOnly = true)
    public Agent getAgentByCode(String agentCode) {
        return agentRepository.findByAgentCode(agentCode)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
    }

    @Transactional(readOnly = true)
    public List<AgentResponse> getAllAgents() {
        return agentRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private AgentResponse mapToResponse(Agent agent) {
        return new AgentResponse(
            agent.getAgentId(),
            agent.getUserId(),
            agent.getAgentCode(),
            agent.getBusinessName(),
            agent.getTerritory(),
            agent.getCommissionRate(),
            agent.getFloatBalance(),
            agent.getStatus(),
            agent.getCreatedAt()
        );
    }
}
