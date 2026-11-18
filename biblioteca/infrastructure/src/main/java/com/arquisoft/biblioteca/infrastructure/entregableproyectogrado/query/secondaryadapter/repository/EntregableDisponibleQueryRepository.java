package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface EntregableDisponibleQueryRepository
        extends SpecificationQueryRepository<EntregableDisponibleJpaQueryEntity, UUID> {
}
