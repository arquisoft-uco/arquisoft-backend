package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaqueteEntregableAccesoQueryRepository
        extends QueryRepository<PaqueteEntregableAccesoJpaQueryEntity, UUID> {

    Optional<PaqueteEntregableAccesoJpaQueryEntity> findByEntregableProyectoGradoId(UUID entregableProyectoGradoId);
}
