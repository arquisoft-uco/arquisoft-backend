package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface TareaQueryRepository extends SpecificationQueryRepository<TareaJpaQueryEntity, UUID> {
}
