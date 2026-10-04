package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface AdministradorQueryRepository
        extends SpecificationQueryRepository<AdministradorJpaQueryEntity, UUID> {
}
