package com.zimmomo.momo.domain.service;

import com.zimmomo.momo.api.dto.request.CreateUserRequest;
import com.zimmomo.momo.api.dto.response.UserResponse;
import com.zimmomo.momo.domain.model.entity.User;
import com.zimmomo.momo.domain.model.entity.Wallet;
import com.zimmomo.momo.domain.model.enums.UserStatus;
import com.zimmomo.momo.domain.model.enums.UserType;
import com.zimmomo.momo.domain.repository.UserRepository;
import com.zimmomo.momo.domain.repository.WalletRepository;
import com.zimmomo.momo.exception.BusinessException;
import com.zimmomo.momo.exception.ResourceNotFoundException;
import com.zimmomo.momo.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BusinessException("Phone number already registered");
        }

        User user = User.builder()
            .phoneNumber(request.phoneNumber())
            .pinHash(passwordEncoder.encode(request.pin()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .nationalId(request.nationalId())
            .userType(request.userType())
            .status(UserStatus.ACTIVE)
            .build();

        user = userRepository.save(user);

        // Auto-create USD wallet for all users
        Wallet wallet = Wallet.builder()
            .userId(user.getUserId())
            .currencyCode("USD")
            .build();
        walletRepository.save(wallet);

        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
            user.getUserId(),
            user.getPhoneNumber(),
            user.getFirstName(),
            user.getLastName(),
            user.getNationalId(),
            user.getKycLevel(),
            user.getUserType(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
