package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionEstudianteQueryRepository
        extends QueryRepository<ObservacionEstudianteJpaQueryEntity, UUID> {

    List<ObservacionEstudianteJpaQueryEntity> findByRevisionAsesorAndEstudianteOrderByDescripcionAscIdAsc(
            UUID revisionAsesor, UUID estudiante);
}
