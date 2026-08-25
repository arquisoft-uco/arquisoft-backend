package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface FichaPerfilQueryRepository
        extends SpecificationQueryRepository<FichaPerfilJpaQueryEntity, UUID> {
}
