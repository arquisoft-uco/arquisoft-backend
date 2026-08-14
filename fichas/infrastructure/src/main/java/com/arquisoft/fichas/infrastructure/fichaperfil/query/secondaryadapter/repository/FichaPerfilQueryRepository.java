package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.postgres.repository.ReadOnlySpecificationRepository;

import java.util.UUID;

public interface FichaPerfilQueryRepository
        extends ReadOnlySpecificationRepository<FichaPerfilJpaQueryEntity, UUID> {
}
