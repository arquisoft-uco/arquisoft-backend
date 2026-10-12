package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.ContextoRegistroEvaluacionJuradoProjection;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.projection.EstadoEvaluacionJuradoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EvaluacionJuradoCommandRepository extends JpaRepository<EvaluacionJuradoJpaEntity, UUID> {

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

    @Query("select ej.jurado from EvaluacionJuradoJpaEntity ej "
            + "where ej.evaluacion = :evaluacion and ej.jurado in :jurados")
    Set<UUID> findJuradosAsignados(@Param("evaluacion") UUID evaluacion, @Param("jurados") Set<UUID> jurados);

    // Mismo bloqueo que buscarContextoBloqueado: el estado leido es precondicion del cambio de
    // puntaje y no puede pasar a finalizada antes del commit de esa escritura.
    @NativeQuery("""
            SELECT ej.id AS id, ej.jurado_id AS jurado, e.estado_evaluacion_id AS estado
            FROM evaluacion_jurado ej
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ej.id = :evaluacionJurado
            FOR UPDATE OF e, ej
            """)
    Optional<EstadoEvaluacionJuradoProjection> buscarEstadoBloqueado(
            @Param("evaluacionJurado") UUID evaluacionJurado);

    @NativeQuery("""
            SELECT (e.estado_evaluacion_id = 'FINALIZADA')
            FROM evaluacion_cuantitativa_jurado ecj
            JOIN evaluacion_jurado ej ON ej.id = ecj.evaluacion_jurado_id
            JOIN evaluacion e ON e.id = ej.evaluacion_id
            WHERE ecj.id = :evaluacionCuantitativaJurado
            """)
    Optional<Boolean> estaFinalizada(@Param("evaluacionCuantitativaJurado") UUID evaluacionCuantitativaJurado);
}
