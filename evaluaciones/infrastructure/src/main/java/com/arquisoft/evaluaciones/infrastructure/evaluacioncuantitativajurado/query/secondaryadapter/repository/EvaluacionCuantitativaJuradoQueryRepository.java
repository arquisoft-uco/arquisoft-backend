package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionCuantitativaJuradoQueryRepository
        extends QueryRepository<EvaluacionCuantitativaJuradoJpaQueryEntity, UUID> {

    List<EvaluacionCuantitativaJuradoJpaQueryEntity> findByEvaluacionJuradoIdOrderByItemNombreAscIdAsc(
            UUID evaluacionJuradoId);
}
