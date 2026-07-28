package com.zimmomo.momo.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "settlement_wallet_id")
    private UUID settlementWalletId;

    @Column(name = "webhook_url", length = 500)
    private String webhookUrl;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
