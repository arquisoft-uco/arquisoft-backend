package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevisionItemCommandRepository extends JpaRepository<RevisionItemEntity, UUID> {

    long countByItemId(UUID itemId);
}
