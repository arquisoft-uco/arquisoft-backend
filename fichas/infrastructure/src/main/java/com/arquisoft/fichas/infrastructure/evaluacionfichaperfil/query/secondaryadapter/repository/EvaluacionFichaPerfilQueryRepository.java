package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryRepository
        extends QueryRepository<EvaluacionFichaPerfilJpaEntity, UUID> {

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);
}
