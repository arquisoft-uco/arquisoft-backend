package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ArtefactoEntregableQueryRepository
        extends QueryRepository<ArtefactoEntregableJpaQueryEntity, UUID> {

    List<ArtefactoEntregableJpaQueryEntity> findByEntregableIdOrderByNombreDocumentoAscIdAsc(UUID entregableId);
}
