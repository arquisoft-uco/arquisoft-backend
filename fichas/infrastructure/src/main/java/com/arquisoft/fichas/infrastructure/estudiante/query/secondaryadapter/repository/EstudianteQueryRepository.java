package com.arquisoft.fichas.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EstudianteQueryRepository extends QueryRepository<EstudianteJpaEntity, UUID> {
}
