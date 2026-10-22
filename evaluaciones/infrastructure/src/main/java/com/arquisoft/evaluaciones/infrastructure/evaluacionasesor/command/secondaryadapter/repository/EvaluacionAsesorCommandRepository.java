package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.projection.ContextoRegistroEvaluacionAsesorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EvaluacionAsesorCommandRepository extends JpaRepository<EvaluacionAsesorJpaEntity, UUID> {

    // FOR UPDATE OF e, ea: el bloqueo cubre el JOIN entre evaluacion y evaluacion_asesor, que @Lock
    // de Spring Data no expresa sobre una unica entidad. El futuro flujo de finalizacion debe
    // adquirir el mismo bloqueo dentro de su propia transaccion.
    @NativeQuery("""
            SELECT ea.id AS id, e.id AS evaluacion,
                   e.estado_evaluacion_id AS estado, e.entregable_id AS entregable,
                   ea.asesor_id AS asesor
            FROM evaluacion_asesor ea
            JOIN evaluacion e ON e.id = ea.evaluacion_id
            WHERE ea.id = :evaluacionAsesor
            FOR UPDATE OF e, ea
            """)
    Optional<ContextoRegistroEvaluacionAsesorProjection> buscarContextoBloqueado(
            @Param("evaluacionAsesor") UUID evaluacionAsesor);
}
