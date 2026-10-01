package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionFichaPerfilQueryRepository
        extends QueryRepository<EvaluacionFichaPerfilJpaQueryEntity, UUID> {

    List<EvaluacionFichaPerfilJpaQueryEntity> findByFichaPerfilIdAndRepresentanteComiteIdOrderByFechaCreacionAsc(
            UUID fichaPerfilId, UUID representanteComiteId);
}
