package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionFichaPerfilEstudianteQueryRepository
        extends QueryRepository<EvaluacionFichaPerfilEstudianteJpaQueryEntity, UUID> {

    List<EvaluacionFichaPerfilEstudianteJpaQueryEntity> findByFichaPerfilIdAndEstudianteIdOrderByFechaCreacionAsc(
            UUID fichaPerfilId, UUID estudianteId);
}
