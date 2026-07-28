package com.zimmomo.momo.domain.service;

import com.zimmomo.momo.api.dto.response.WalletResponse;
import com.zimmomo.momo.domain.model.entity.Wallet;
import com.zimmomo.momo.domain.repository.WalletRepository;
import com.zimmomo.momo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(readOnly = true)
    public List<WalletResponse> getUserWallets(UUID userId) {
        return walletRepository.findByUserId(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return mapToResponse(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletEntity(UUID walletId) {
        return walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletByUserAndCurrency(UUID userId, String currencyCode) {
        Wallet wallet = walletRepository.findByUserIdAndCurrencyCode(userId, currencyCode)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for currency: " + currencyCode));
        return mapToResponse(wallet);
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getWalletId(),
            wallet.getUserId(),
            wallet.getCurrencyCode(),
            wallet.getBalance(),
            wallet.getDailyLimit(),
            wallet.getMonthlyLimit(),
            wallet.getIsActive(),
            wallet.getCreatedAt()
        );
    }
}
