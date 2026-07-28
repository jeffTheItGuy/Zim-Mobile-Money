package com.zimmomo.momo.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_TTL_MS = 10000;
    private static final long RETRY_DELAY_MS = 100;
    private static final int MAX_RETRIES = 50;

    public <T> T executeWithLock(UUID resourceId, Supplier<T> action) {
        String lockKey = LOCK_PREFIX + resourceId.toString();
        String lockValue = UUID.randomUUID().toString();

        boolean acquired = acquireLock(lockKey, lockValue, DEFAULT_LOCK_TTL_MS);
        if (!acquired) {
            throw new RuntimeException("Could not acquire lock on resource: " + resourceId);
        }

        try {
            return action.get();
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    public void executeWithLock(UUID resourceId, Runnable action) {
        executeWithLock(resourceId, () -> {
            action.run();
            return null;
        });
    }

    private boolean acquireLock(String lockKey, String lockValue, long ttlMs) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, ttlMs, TimeUnit.MILLISECONDS);

            if (Boolean.TRUE.equals(acquired)) {
                return true;
            }

            retries++;
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private void releaseLock(String lockKey, String lockValue) {
        try {
            String currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            log.warn("Failed to release lock {}: {}", lockKey, e.getMessage());
        }
    }
}
