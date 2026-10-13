package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ObservacionEvaluacionCommandRepository extends JpaRepository<ObservacionEvaluacionJpaEntity, UUID> {

    boolean existsByEvaluacionFichaPerfilIdAndObservacion(UUID evaluacionFichaPerfilId, String observacion);

    @Query("""
            SELECT new com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.PertenenciaObservacionEvaluacionEntity(
                o.evaluacionFichaPerfilId,
                CASE WHEN ev.representanteComiteId = :representanteComite THEN true ELSE false END,
                e.estadoEvaluacion.id)
            FROM ObservacionEvaluacionJpaEntity o
            JOIN EvaluacionFichaPerfilJpaEntity ev ON ev.id = o.evaluacionFichaPerfilId
            LEFT JOIN EstadoEvaluacionFichaJpaEntity e
                ON e.evaluacionFichaPerfil.id = o.evaluacionFichaPerfilId
                AND NOT EXISTS (
                    SELECT 1 FROM EstadoEvaluacionFichaJpaEntity e2
                    WHERE e2.evaluacionFichaPerfil.id = o.evaluacionFichaPerfilId
                    AND (e2.fechaActualizacion > e.fechaActualizacion
                        OR (e2.fechaActualizacion = e.fechaActualizacion AND e2.id > e.id)))
            WHERE o.id = :observacionEvaluacion
            """)
    Optional<PertenenciaObservacionEvaluacionEntity> obtenerPertenencia(
            @Param("observacionEvaluacion") UUID observacionEvaluacion,
            @Param("representanteComite") UUID representanteComite);

    @Query("""
            SELECT CASE WHEN COUNT(o2) > 0 THEN true ELSE false END
            FROM ObservacionEvaluacionJpaEntity o
            JOIN ObservacionEvaluacionJpaEntity o2 ON o2.evaluacionFichaPerfilId = o.evaluacionFichaPerfilId
            WHERE o.id = :observacionEvaluacion AND o2.id <> o.id AND o2.observacion = :observacion
            """)
    boolean existeOtraConMismoTexto(@Param("observacionEvaluacion") UUID observacionEvaluacion,
                                    @Param("observacion") String observacion);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ObservacionEvaluacionJpaEntity o SET o.observacion = :observacion WHERE o.id = :id")
    int actualizarObservacion(@Param("id") UUID id, @Param("observacion") String observacion);
}
