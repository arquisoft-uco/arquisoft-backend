package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservacionItemCommandRepository extends JpaRepository<ObservacionItemJpaEntity, UUID> {

    long countByRevisionItemIdAndObservacion(UUID revisionItemId, String observacion);
}
