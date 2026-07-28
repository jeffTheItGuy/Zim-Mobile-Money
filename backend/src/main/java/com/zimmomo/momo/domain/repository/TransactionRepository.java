package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.Transaction;
import com.zimmomo.momo.domain.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    List<Transaction> findBySenderWalletIdOrReceiverWalletId(UUID senderWalletId, UUID receiverWalletId);

    @Query("SELECT t FROM Transaction t WHERE t.senderWalletId = :walletId OR t.receiverWalletId = :walletId ORDER BY t.createdAt DESC")
    Page<Transaction> findByWalletId(@Param("walletId") UUID walletId, Pageable pageable);

    @Modifying
    @Query("UPDATE Transaction t SET t.status = :status, t.completedAt = :completedAt WHERE t.transactionId = :transactionId")
    int updateStatus(@Param("transactionId") UUID transactionId, @Param("status") TransactionStatus status, @Param("completedAt") Instant completedAt);
}
