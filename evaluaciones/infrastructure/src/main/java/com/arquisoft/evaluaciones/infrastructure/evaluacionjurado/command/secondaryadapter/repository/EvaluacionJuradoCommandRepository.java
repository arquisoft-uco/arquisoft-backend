package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.ContextoRegistroEvaluacionJuradoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
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

    @Query(value = """
            SELECT (e.estado_evaluacion_id = 'FINALIZADA')
            FROM observacion_item_jurado o
            JOIN evaluacion_cuantitativa_jurado ecj ON ecj.id = o.evaluacion_cuantitativa_jurado_id
            JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE o.id = :observacion
            """, nativeQuery = true)
    Optional<Boolean> estaFinalizadaPorObservacion(@Param("observacion") UUID observacion);

    // FOR UPDATE OF e, ej: el bloqueo cubre el JOIN entre evaluacion y evaluacion_jurado, que @Lock
    // de Spring Data no expresa sobre una unica entidad. El futuro flujo de finalizacion debe
    // adquirir el mismo bloqueo dentro de su propia transaccion.
    @NativeQuery("""
            SELECT ej.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado
            FOR UPDATE OF e, ej
            """)
    Optional<ContextoRegistroEvaluacionJuradoProjection> buscarContextoBloqueado(
            @Param("evaluacionJurado") UUID evaluacionJurado);
}
