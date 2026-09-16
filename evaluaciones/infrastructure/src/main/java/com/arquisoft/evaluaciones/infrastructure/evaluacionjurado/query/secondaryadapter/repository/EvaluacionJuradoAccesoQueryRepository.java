package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionJuradoAccesoQueryRepository
        extends QueryRepository<EvaluacionJuradoJpaQueryEntity, UUID> {

    @Query(value = """
            SELECT en.proyecto
            FROM evaluacion_jurado ej
            JOIN evaluacion ev ON ev.id = ej.evaluacion_id
            JOIN entregable en ON en.id = ev.entregable_id
            WHERE ej.id = :evaluacionJurado
            """, nativeQuery = true)
    Optional<String> obtenerProyecto(@Param("evaluacionJurado") UUID evaluacionJurado);
}
