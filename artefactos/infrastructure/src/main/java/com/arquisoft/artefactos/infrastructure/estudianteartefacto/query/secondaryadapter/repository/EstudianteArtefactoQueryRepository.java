package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoQueryRepository
        extends QueryRepository<EstudianteArtefactoJpaQueryEntity, UUID> {

    List<EstudianteArtefactoJpaQueryEntity> findByArtefactoIdOrderByNombreAscIdAsc(UUID artefactoId);
}
