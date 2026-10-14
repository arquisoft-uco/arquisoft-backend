package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.ContextoAsignacionEvaluacionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionCommandRepository extends JpaRepository<EvaluacionJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJpaEntity evaluacion "
            + "set evaluacion.estadoEvaluacion = :estado where evaluacion.id = :id")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);

    // El bloqueo debe ser el mismo que adquirira el futuro flujo de finalizacion.
    @NativeQuery("""
            SELECT e.id AS evaluacion, e.estado_evaluacion_id AS estado
            FROM evaluacion e
            WHERE e.id = :evaluacion
            FOR UPDATE
            """)
    Optional<ContextoAsignacionEvaluacionProjection> buscarContextoAsignacionBloqueado(
            @Param("evaluacion") UUID evaluacion);
}
