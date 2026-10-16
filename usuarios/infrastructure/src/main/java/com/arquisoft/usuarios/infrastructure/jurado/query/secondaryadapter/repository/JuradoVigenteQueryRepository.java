package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface JuradoVigenteQueryRepository
        extends SpecificationQueryRepository<JuradoVigenteJpaQueryEntity, UUID> {
}
