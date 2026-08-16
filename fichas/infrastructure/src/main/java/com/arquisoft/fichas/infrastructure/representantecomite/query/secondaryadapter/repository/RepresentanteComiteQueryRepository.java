package com.arquisoft.fichas.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface RepresentanteComiteQueryRepository
        extends QueryRepository<RepresentanteComiteJpaEntity, UUID> {
}
