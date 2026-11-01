package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ObservacionQueryRepository extends QueryRepository<ObservacionJpaQueryEntity, UUID> {

    List<ObservacionJpaQueryEntity> findByRevisionAsesorAndAsesorOrderByDescripcionAscIdAsc(
            UUID revisionAsesor, UUID asesor);
}
