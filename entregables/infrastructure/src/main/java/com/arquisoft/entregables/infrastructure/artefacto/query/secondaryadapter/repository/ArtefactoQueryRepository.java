package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ArtefactoQueryRepository extends QueryRepository<ArtefactoJpaQueryEntity, UUID> {

    List<ArtefactoJpaQueryEntity> findByProyectoGradoIdOrderByNombreDocumentoAscIdAsc(UUID proyectoGradoId);
}
