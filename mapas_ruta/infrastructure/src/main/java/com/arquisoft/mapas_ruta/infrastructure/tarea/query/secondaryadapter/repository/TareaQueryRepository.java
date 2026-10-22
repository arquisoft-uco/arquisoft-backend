package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface TareaQueryRepository extends QueryRepository<TareaJpaQueryEntity, UUID> {
}
