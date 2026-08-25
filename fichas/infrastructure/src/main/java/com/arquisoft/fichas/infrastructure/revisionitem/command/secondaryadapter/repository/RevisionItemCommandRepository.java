package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevisionItemCommandRepository extends JpaRepository<RevisionItemJpaEntity, UUID> {

    long countByItemId(UUID itemId);
}
