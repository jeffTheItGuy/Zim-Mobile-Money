package com.zimmomo.momo.transaction;

import com.zimmomo.momo.api.dto.request.CashInRequest;
import com.zimmomo.momo.api.dto.request.CashOutRequest;
import com.zimmomo.momo.api.dto.request.TransferRequest;
import com.zimmomo.momo.api.dto.response.TransactionResponse;
import com.zimmomo.momo.audit.AuditService;
import com.zimmomo.momo.concurrency.LockService;
import com.zimmomo.momo.domain.model.entity.*;
import com.zimmomo.momo.domain.model.enums.*;
import com.zimmomo.momo.domain.repository.*;
import com.zimmomo.momo.exception.BusinessException;
import com.zimmomo.momo.exception.ResourceNotFoundException;
import com.zimmomo.momo.idempotency.IdempotencyService;
import com.zimmomo.momo.ledger.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final AgentFloatLogRepository agentFloatLogRepository;
    private final LedgerService ledgerService;
    private final IdempotencyService idempotencyService;
    private final LockService lockService;
    private final AuditService auditService;

    private static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.005");
    private static final BigDecimal CASH_IN_FEE = BigDecimal.ZERO;
    private static final BigDecimal CASH_OUT_FEE = new BigDecimal("0.50");

    @Transactional
    public TransactionResponse transfer(UUID senderUserId, TransferRequest request, SourceChannel channel) {
        idempotencyService.checkAndStore(request.idempotencyKey());

        User sender = userRepository.findById(senderUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findByPhoneNumber(request.recipientPhoneNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Wallet senderWallet = walletRepository.findByUserIdAndCurrencyCode(senderUserId, request.currencyCode())
            .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUserIdAndCurrencyCode(receiver.getUserId(), request.currencyCode())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

        if (!senderWallet.getIsActive() || !receiverWallet.getIsActive()) {
            throw new BusinessException("Wallet is inactive");
        }

        BigDecimal fee = request.amount().multiply(TRANSFER_FEE_RATE).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalDebit = request.amount().add(fee);

        if (senderWallet.getBalance().compareTo(totalDebit) < 0) {
            throw new BusinessException("Insufficient balance");
        }

        UUID firstLock = senderWallet.getWalletId().compareTo(receiverWallet.getWalletId()) < 0
            ? senderWallet.getWalletId() : receiverWallet.getWalletId();
        UUID secondLock = senderWallet.getWalletId().compareTo(receiverWallet.getWalletId()) < 0
            ? receiverWallet.getWalletId() : senderWallet.getWalletId();

        lockService.executeWithLock(firstLock, () ->
            lockService.executeWithLock(secondLock, () -> {
                Wallet refreshedSender = walletRepository.findById(senderWallet.getWalletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

                if (refreshedSender.getBalance().compareTo(totalDebit) < 0) {
                    throw new BusinessException("Insufficient balance after lock acquisition");
                }

                Transaction txn = Transaction.builder()
                    .referenceNumber(generateReference())
                    .idempotencyKey(request.idempotencyKey())
                    .transactionType(TransactionType.TRANSFER)
                    .status(TransactionStatus.PENDING)
                    .amount(request.amount())
                    .feeAmount(fee)
                    .currencyCode(request.currencyCode())
                    .senderWalletId(senderWallet.getWalletId())
                    .receiverWalletId(receiverWallet.getWalletId())
                    .sourceChannel(channel)
                    .description(request.description())
                    .build();

                txn = transactionRepository.save(txn);

                try {
                    int debited = walletRepository.debitWallet(senderWallet.getWalletId(), totalDebit);
                    if (debited == 0) throw new BusinessException("Debit failed");

                    int credited = walletRepository.creditWallet(receiverWallet.getWalletId(), request.amount());
                    if (credited == 0) throw new BusinessException("Credit failed");

                    ledgerService.postTransaction(
                        txn.getTransactionId(),
                        senderWallet.getWalletId(),
                        receiverWallet.getWalletId(),
                        request.amount(),
                        fee,
                        BigDecimal.ZERO,
                        request.currencyCode(),
                        request.description()
                    );

                    txn.setStatus(TransactionStatus.COMPLETED);
                    txn.setCompletedAt(Instant.now());
                    txn = transactionRepository.save(txn);

                    auditService.log(senderUserId, "TRANSFER_COMPLETED",
                        String.format("Transferred %s %s to %s", request.amount(), request.currencyCode(), request.recipientPhoneNumber()));

                    return txn;
                } catch (Exception e) {
                    txn.setStatus(TransactionStatus.FAILED);
                    txn.setFailureReason(e.getMessage());
                    transactionRepository.save(txn);
                    throw new BusinessException("Transfer failed: " + e.getMessage());
                }
            })
        );

        return mapToResponse(transactionRepository.findByIdempotencyKey(request.idempotencyKey()).orElseThrow());
    }

    @Transactional
    public TransactionResponse cashIn(UUID agentUserId, CashInRequest request, SourceChannel channel) {
        idempotencyService.checkAndStore(request.idempotencyKey());

        Agent agent = agentRepository.findByUserId(agentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (agent.getStatus() != AgentStatus.ACTIVE) {
            throw new BusinessException("Agent is not active");
        }

        User customer = userRepository.findByPhoneNumber(request.customerPhoneNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Wallet customerWallet = walletRepository.findByUserIdAndCurrencyCode(customer.getUserId(), request.currencyCode())
            .orElseThrow(() -> new ResourceNotFoundException("Customer wallet not found"));

        if (agent.getFloatBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException("Agent has insufficient float");
        }

        lockService.executeWithLock(agent.getAgentId(), () ->
            lockService.executeWithLock(customerWallet.getWalletId(), () -> {
                Agent refreshedAgent = agentRepository.findById(agent.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

                if (refreshedAgent.getFloatBalance().compareTo(request.amount()) < 0) {
                    throw new BusinessException("Agent float insufficient after lock");
                }

                Transaction txn = Transaction.builder()
                    .referenceNumber(generateReference())
                    .idempotencyKey(request.idempotencyKey())
                    .transactionType(TransactionType.CASH_IN)
                    .status(TransactionStatus.PENDING)
                    .amount(request.amount())
                    .feeAmount(CASH_IN_FEE)
                    .currencyCode(request.currencyCode())
                    .receiverWalletId(customerWallet.getWalletId())
                    .agentId(agent.getAgentId())
                    .sourceChannel(channel)
                    .description(request.description())
                    .build();

                txn = transactionRepository.save(txn);

                try {
                    int deducted = agentRepository.deductFloat(agent.getAgentId(), request.amount());
                    if (deducted == 0) throw new BusinessException("Float deduction failed");

                    int credited = walletRepository.creditWallet(customerWallet.getWalletId(), request.amount());
                    if (credited == 0) throw new BusinessException("Wallet credit failed");

                    AgentFloatLog floatLog = AgentFloatLog.builder()
                        .agentId(agent.getAgentId())
                        .transactionId(txn.getTransactionId())
                        .movementType(FloatMovementType.CASH_IN)
                        .amount(request.amount().negate())
                        .runningFloat(refreshedAgent.getFloatBalance().subtract(request.amount()))
                        .build();
                    agentFloatLogRepository.save(floatLog);

                    ledgerService.postTransaction(
                        txn.getTransactionId(),
                        null,
                        customerWallet.getWalletId(),
                        request.amount(),
                        CASH_IN_FEE,
                        BigDecimal.ZERO,
                        request.currencyCode(),
                        "Cash-in via agent " + agent.getAgentCode()
                    );

                    txn.setStatus(TransactionStatus.COMPLETED);
                    txn.setCompletedAt(Instant.now());
                    txn = transactionRepository.save(txn);

                    auditService.log(agentUserId, "CASH_IN_COMPLETED",
                        String.format("Cash-in %s %s for %s", request.amount(), request.currencyCode(), request.customerPhoneNumber()));

                    return txn;
                } catch (Exception e) {
                    txn.setStatus(TransactionStatus.FAILED);
                    txn.setFailureReason(e.getMessage());
                    transactionRepository.save(txn);
                    throw new BusinessException("Cash-in failed: " + e.getMessage());
                }
            })
        );

        return mapToResponse(transactionRepository.findByIdempotencyKey(request.idempotencyKey()).orElseThrow());
    }

    @Transactional
    public TransactionResponse cashOut(UUID agentUserId, CashOutRequest request, SourceChannel channel) {
        idempotencyService.checkAndStore(request.idempotencyKey());

        Agent agent = agentRepository.findByUserId(agentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        if (agent.getStatus() != AgentStatus.ACTIVE) {
            throw new BusinessException("Agent is not active");
        }

        User customer = userRepository.findByPhoneNumber(request.customerPhoneNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Wallet customerWallet = walletRepository.findByUserIdAndCurrencyCode(customer.getUserId(), request.currencyCode())
            .orElseThrow(() -> new ResourceNotFoundException("Customer wallet not found"));

        BigDecimal totalDebit = request.amount().add(CASH_OUT_FEE);

        if (customerWallet.getBalance().compareTo(totalDebit) < 0) {
            throw new BusinessException("Customer has insufficient balance");
        }

        lockService.executeWithLock(customerWallet.getWalletId(), () ->
            lockService.executeWithLock(agent.getAgentId(), () -> {
                Wallet refreshedWallet = walletRepository.findById(customerWallet.getWalletId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

                if (refreshedWallet.getBalance().compareTo(totalDebit) < 0) {
                    throw new BusinessException("Insufficient balance after lock");
                }

                Transaction txn = Transaction.builder()
                    .referenceNumber(generateReference())
                    .idempotencyKey(request.idempotencyKey())
                    .transactionType(TransactionType.CASH_OUT)
                    .status(TransactionStatus.PENDING)
                    .amount(request.amount())
                    .feeAmount(CASH_OUT_FEE)
                    .currencyCode(request.currencyCode())
                    .senderWalletId(customerWallet.getWalletId())
                    .agentId(agent.getAgentId())
                    .sourceChannel(channel)
                    .description(request.description())
                    .build();

                txn = transactionRepository.save(txn);

                try {
                    int debited = walletRepository.debitWallet(customerWallet.getWalletId(), totalDebit);
                    if (debited == 0) throw new BusinessException("Wallet debit failed");

                    int added = agentRepository.addFloat(agent.getAgentId(), request.amount());
                    if (added == 0) throw new BusinessException("Float addition failed");

                    Agent refreshedAgent = agentRepository.findById(agent.getAgentId()).orElseThrow();
                    AgentFloatLog floatLog = AgentFloatLog.builder()
                        .agentId(agent.getAgentId())
                        .transactionId(txn.getTransactionId())
                        .movementType(FloatMovementType.CASH_OUT)
                        .amount(request.amount())
                        .runningFloat(refreshedAgent.getFloatBalance())
                        .build();
                    agentFloatLogRepository.save(floatLog);

                    ledgerService.postTransaction(
                        txn.getTransactionId(),
                        customerWallet.getWalletId(),
                        null,
                        request.amount(),
                        CASH_OUT_FEE,
                        BigDecimal.ZERO,
                        request.currencyCode(),
                        "Cash-out via agent " + agent.getAgentCode()
                    );

                    txn.setStatus(TransactionStatus.COMPLETED);
                    txn.setCompletedAt(Instant.now());
                    txn = transactionRepository.save(txn);

                    auditService.log(agentUserId, "CASH_OUT_COMPLETED",
                        String.format("Cash-out %s %s for %s", request.amount(), request.currencyCode(), request.customerPhoneNumber()));

                    return txn;
                } catch (Exception e) {
                    txn.setStatus(TransactionStatus.FAILED);
                    txn.setFailureReason(e.getMessage());
                    transactionRepository.save(txn);
                    throw new BusinessException("Cash-out failed: " + e.getMessage());
                }
            })
        );

        return mapToResponse(transactionRepository.findByIdempotencyKey(request.idempotencyKey()).orElseThrow());
    }

    @Transactional
    public TransactionResponse topUpAgentFloat(UUID adminUserId, String agentCode, BigDecimal amount, String currencyCode, String idempotencyKey) {
        idempotencyService.checkAndStore(idempotencyKey);

        Agent agent = agentRepository.findByAgentCode(agentCode)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        lockService.executeWithLock(agent.getAgentId(), () -> {
            int added = agentRepository.addFloat(agent.getAgentId(), amount);
            if (added == 0) throw new BusinessException("Float top-up failed");

            Agent refreshedAgent = agentRepository.findById(agent.getAgentId()).orElseThrow();
            AgentFloatLog floatLog = AgentFloatLog.builder()
                .agentId(agent.getAgentId())
                .movementType(FloatMovementType.TOP_UP)
                .amount(amount)
                .runningFloat(refreshedAgent.getFloatBalance())
                .build();
            agentFloatLogRepository.save(floatLog);

            auditService.log(adminUserId, "AGENT_FLOAT_TOPUP",
                String.format("Topped up agent %s with %s %s", agentCode, amount, currencyCode));

            return null;
        });

        return new TransactionResponse(
            null, "FLOAT-" + agentCode, TransactionType.CASH_IN, TransactionStatus.COMPLETED,
            amount, BigDecimal.ZERO, BigDecimal.ZERO, currencyCode,
            null, null, agent.getAgentId(), SourceChannel.WEB,
            "Agent float top-up", Instant.now(), Instant.now()
        );
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getWalletTransactions(UUID walletId, Pageable pageable) {
        return transactionRepository.findByWalletId(walletId, pageable)
            .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(UUID transactionId) {
        Transaction txn = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToResponse(txn);
    }

    private String generateReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TransactionResponse mapToResponse(Transaction txn) {
        return new TransactionResponse(
            txn.getTransactionId(),
            txn.getReferenceNumber(),
            txn.getTransactionType(),
            txn.getStatus(),
            txn.getAmount(),
            txn.getFeeAmount(),
            txn.getAgentCommission(),
            txn.getCurrencyCode(),
            txn.getSenderWalletId(),
            txn.getReceiverWalletId(),
            txn.getAgentId(),
            txn.getSourceChannel(),
            txn.getDescription(),
            txn.getCreatedAt(),
            txn.getCompletedAt()
        );
    }
}
