package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.shared.postgres.repository.ReadOnlyRepository;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryRepository
        extends ReadOnlyRepository<EvaluacionFichaPerfilEntity, UUID> {

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);
}
