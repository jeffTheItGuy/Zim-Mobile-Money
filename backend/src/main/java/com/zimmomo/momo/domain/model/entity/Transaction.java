package com.zimmomo.momo.domain.model.entity;

import com.zimmomo.momo.domain.model.enums.SourceChannel;
import com.zimmomo.momo.domain.model.enums.TransactionStatus;
import com.zimmomo.momo.domain.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "fee_amount", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "agent_commission", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal agentCommission = BigDecimal.ZERO;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "sender_wallet_id")
    private UUID senderWalletId;

    @Column(name = "receiver_wallet_id")
    private UUID receiverWalletId;

    @Column(name = "agent_id")
    private UUID agentId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_channel", nullable = false, length = 20)
    private SourceChannel sourceChannel;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "external_reference", length = 100)
    private String externalReference;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "reversed_at")
    private Instant reversedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
