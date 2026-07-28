package com.zimmomo.momo.ledger;

import com.zimmomo.momo.domain.model.entity.LedgerEntry;
import com.zimmomo.momo.domain.model.enums.EntryType;
import com.zimmomo.momo.domain.repository.LedgerEntryRepository;
import com.zimmomo.momo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public List<LedgerEntry> postTransaction(UUID transactionId, UUID senderWalletId, UUID receiverWalletId,
                                             BigDecimal amount, BigDecimal feeAmount, BigDecimal commissionAmount,
                                             String currencyCode, String description) {

        List<LedgerEntry> entries = new ArrayList<>();

        if (senderWalletId != null) {
            BigDecimal senderBalance = getRunningBalance(senderWalletId);
            BigDecimal totalDebit = amount.add(feeAmount);

            if (senderBalance.compareTo(totalDebit) < 0) {
                throw new BusinessException("Insufficient funds for ledger posting");
            }

            entries.add(LedgerEntry.builder()
                .transactionId(transactionId)
                .walletId(senderWalletId)
                .entryType(EntryType.DEBIT)
                .amount(totalDebit)
                .runningBalance(senderBalance.subtract(totalDebit))
                .description("Debit: " + description)
                .build());
        }

        if (receiverWalletId != null) {
            BigDecimal receiverBalance = getRunningBalance(receiverWalletId);
            BigDecimal creditAmount = amount.subtract(commissionAmount);

            entries.add(LedgerEntry.builder()
                .transactionId(transactionId)
                .walletId(receiverWalletId)
                .entryType(EntryType.CREDIT)
                .amount(creditAmount)
                .runningBalance(receiverBalance.add(creditAmount))
                .description("Credit: " + description)
                .build());
        }

        BigDecimal totalDebits = entries.stream()
            .filter(e -> e.getEntryType() == EntryType.DEBIT)
            .map(LedgerEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = entries.stream()
            .filter(e -> e.getEntryType() == EntryType.CREDIT)
            .map(LedgerEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebits.compareTo(totalCredits.add(feeAmount).add(commissionAmount)) != 0) {
            throw new BusinessException("Ledger imbalance detected");
        }

        return ledgerEntryRepository.saveAll(entries);
    }

    @Transactional(readOnly = true)
    public BigDecimal getRunningBalance(UUID walletId) {
        BigDecimal balance = ledgerEntryRepository.findLatestBalanceByWalletId(walletId);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getLedgerByWallet(UUID walletId) {
        return ledgerEntryRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getLedgerByTransaction(UUID transactionId) {
        return ledgerEntryRepository.findByTransactionId(transactionId);
    }
}
