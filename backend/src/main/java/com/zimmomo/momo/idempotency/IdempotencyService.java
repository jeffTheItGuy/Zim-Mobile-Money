package com.zimmomo.momo.idempotency;

import com.zimmomo.momo.domain.model.entity.IdempotencyKey;
import com.zimmomo.momo.domain.repository.IdempotencyKeyRepository;
import com.zimmomo.momo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_PREFIX = "idempotency:";
    private static final long REDIS_TTL_SECONDS = 86400;

    @Transactional
    public void checkAndStore(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BusinessException("Idempotency key is required");
        }

        String hash = hashKey(idempotencyKey);
        String redisKey = REDIS_PREFIX + hash;

        Boolean existsInRedis = redisTemplate.hasKey(redisKey);
        if (Boolean.TRUE.equals(existsInRedis)) {
            throw new BusinessException("Duplicate request: idempotency key already used");
        }

        if (idempotencyKeyRepository.existsById(hash)) {
            redisTemplate.opsForValue().set(redisKey, "1", REDIS_TTL_SECONDS, TimeUnit.SECONDS);
            throw new BusinessException("Duplicate request: idempotency key already used");
        }

        redisTemplate.opsForValue().set(redisKey, "1", REDIS_TTL_SECONDS, TimeUnit.SECONDS);

        IdempotencyKey key = IdempotencyKey.builder()
            .keyHash(hash)
            .createdAt(Instant.now())
            .build();
        idempotencyKeyRepository.save(key);
    }

    @Transactional
    public void cleanupOldKeys(int daysToKeep) {
        Instant cutoff = Instant.now().minus(daysToKeep, ChronoUnit.DAYS);
        var oldKeys = idempotencyKeyRepository.findByCreatedAtBefore(cutoff);
        idempotencyKeyRepository.deleteAll(oldKeys);
    }

    private String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
