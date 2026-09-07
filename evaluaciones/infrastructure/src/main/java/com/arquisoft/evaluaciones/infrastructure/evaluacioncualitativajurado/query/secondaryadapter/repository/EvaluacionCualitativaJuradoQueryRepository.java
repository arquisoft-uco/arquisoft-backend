package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCualitativaJuradoQueryRepository
        extends QueryRepository<EvaluacionCualitativaJuradoJpaQueryEntity, UUID> {

    List<EvaluacionCualitativaJuradoJpaQueryEntity> findByEvaluacionJuradoIdAndEstudianteIdOrderByItemNombreAscIdAsc(
            UUID evaluacionJuradoId, UUID estudianteId);
}
