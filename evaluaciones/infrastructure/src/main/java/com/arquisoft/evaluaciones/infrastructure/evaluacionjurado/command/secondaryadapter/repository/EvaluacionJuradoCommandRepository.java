package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionJuradoCommandRepository extends JpaRepository<EvaluacionJuradoJpaEntity, UUID> {

    @Query(value = """
            SELECT (ej.jurado_id = :jurado) AS pertenece,
                   (e.estado_evaluacion_id = 'FINALIZADA') AS finalizada
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado
            """, nativeQuery = true)
    Optional<EstadoEvaluacionJuradoProjection> obtenerEstado(
            @Param("evaluacionJurado") UUID evaluacionJurado, @Param("jurado") UUID jurado);

    @Query(value = """
            SELECT (e.estado_evaluacion_id = 'FINALIZADA')
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado
            """, nativeQuery = true)
    Optional<Boolean> estaFinalizada(@Param("evaluacionJurado") UUID evaluacionJurado);
}
