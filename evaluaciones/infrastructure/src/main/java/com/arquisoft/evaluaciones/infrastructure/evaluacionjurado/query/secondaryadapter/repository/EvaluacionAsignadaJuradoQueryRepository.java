package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluacionAsignadaJuradoQueryRepository
        extends QueryRepository<EvaluacionAsignadaJuradoJpaQueryEntity, UUID> {

    List<EvaluacionAsignadaJuradoJpaQueryEntity>
            findByJuradoIdOrderByEstadoOrdenAscEntregableProyectoAscEntregableVersionAscIdAsc(UUID juradoId);
}
