package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByTransactionId(UUID transactionId);
    List<LedgerEntry> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    @Query("SELECT le.runningBalance FROM LedgerEntry le WHERE le.walletId = :walletId ORDER BY le.createdAt DESC LIMIT 1")
    BigDecimal findLatestBalanceByWalletId(@Param("walletId") UUID walletId);
}
