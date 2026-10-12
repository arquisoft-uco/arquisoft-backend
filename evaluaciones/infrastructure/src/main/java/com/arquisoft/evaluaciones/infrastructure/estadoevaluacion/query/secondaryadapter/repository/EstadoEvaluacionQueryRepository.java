package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;

public interface EstadoEvaluacionQueryRepository
        extends QueryRepository<EstadoEvaluacionJpaQueryEntity, String> {

    List<EstadoEvaluacionJpaQueryEntity> findAllByOrderByOrdenAsc();
}
