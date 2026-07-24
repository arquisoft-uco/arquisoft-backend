package com.arquisoft.fichas.infrastructure.revisionitem.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevisionItemJpaRepository extends JpaRepository<RevisionItemJpaEntity, UUID> {

    long countByItemId(UUID itemId);
}
