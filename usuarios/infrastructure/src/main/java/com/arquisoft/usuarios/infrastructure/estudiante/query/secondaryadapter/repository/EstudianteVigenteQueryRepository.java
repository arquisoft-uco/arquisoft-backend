package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface EstudianteVigenteQueryRepository
        extends SpecificationQueryRepository<EstudianteVigenteJpaQueryEntity, UUID> {
}
