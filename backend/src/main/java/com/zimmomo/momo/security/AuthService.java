package com.zimmomo.momo.security;

import com.zimmomo.momo.api.dto.request.AuthRequest;
import com.zimmomo.momo.api.dto.response.AuthResponse;
import com.zimmomo.momo.domain.model.entity.User;
import com.zimmomo.momo.domain.repository.UserRepository;
import com.zimmomo.momo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByPhoneNumber(request.phoneNumber())
            .orElseThrow(() -> new BusinessException("Invalid phone number or PIN"));

        if (!passwordEncoder.matches(request.pin(), user.getPinHash())) {
            throw new BusinessException("Invalid phone number or PIN");
        }

        String token = jwtUtil.generateToken(
            user.getUserId(),
            user.getPhoneNumber(),
            user.getUserType().name()
        );

        return new AuthResponse(token, "Bearer", 86400L);
    }
}
