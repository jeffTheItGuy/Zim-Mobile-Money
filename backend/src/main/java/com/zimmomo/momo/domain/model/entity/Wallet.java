package com.zimmomo.momo.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "currency_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wallet_id")
    private UUID walletId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "balance", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "daily_limit", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal dailyLimit = new BigDecimal("5000.00");

    @Column(name = "monthly_limit", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal monthlyLimit = new BigDecimal("50000.00");

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
