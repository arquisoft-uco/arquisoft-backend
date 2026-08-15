package com.arquisoft.fichas.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.shared.postgres.repository.QueryRepository;

import java.util.UUID;

public interface EstudianteQueryRepository extends QueryRepository<EstudianteEntity, UUID> {
}
