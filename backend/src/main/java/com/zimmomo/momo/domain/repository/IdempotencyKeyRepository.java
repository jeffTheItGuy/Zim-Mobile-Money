package com.zimmomo.momo.domain.repository;

import com.zimmomo.momo.domain.model.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    List<IdempotencyKey> findByCreatedAtBefore(Instant cutoff);
}
