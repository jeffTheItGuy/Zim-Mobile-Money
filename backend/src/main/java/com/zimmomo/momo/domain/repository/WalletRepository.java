package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    List<Wallet> findByUserId(UUID userId);
    Optional<Wallet> findByUserIdAndCurrencyCode(UUID userId, String currencyCode);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.walletId = :walletId AND w.isActive = true")
    int creditWallet(@Param("walletId") UUID walletId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount WHERE w.walletId = :walletId AND w.balance >= :amount AND w.isActive = true")
    int debitWallet(@Param("walletId") UUID walletId, @Param("amount") BigDecimal amount);
}
