package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RevisionItemCommandRepository extends JpaRepository<RevisionItemJpaEntity, UUID> {

    long countByItemId(UUID itemId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RevisionItemJpaEntity r SET r.estadoRevision = :estadoRevision WHERE r.itemId = :item")
    int actualizarEstadoRevision(@Param("item") UUID item,
                                  @Param("estadoRevision") EstadoRevisionJpaEntity estadoRevision);
}
