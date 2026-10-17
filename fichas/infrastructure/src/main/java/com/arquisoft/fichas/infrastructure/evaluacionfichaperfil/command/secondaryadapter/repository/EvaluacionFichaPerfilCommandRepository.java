package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvaluacionFichaPerfilCommandRepository
        extends JpaRepository<EvaluacionFichaPerfilJpaEntity, UUID> {

    boolean existsByRepresentanteComiteIdAndFichaPerfilId(
            UUID representanteComiteId,
            UUID fichaPerfilId);

    boolean existsByIdAndRepresentanteComiteId(
            UUID id,
            UUID representanteComiteId);

    @Query("""
            SELECT new com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity(
                s.estadoEvaluacion.id,
                COUNT(DISTINCT e.id),
                COUNT(DISTINCT CASE WHEN o.id IS NOT NULL THEN e.id END))
            FROM EvaluacionFichaPerfilJpaEntity e
            JOIN EstadoEvaluacionFichaJpaEntity s ON s.evaluacionFichaPerfil.id = e.id
            LEFT JOIN ObservacionEvaluacionJpaEntity o ON o.evaluacionFichaPerfilId = e.id
            WHERE e.fichaPerfilId = :fichaPerfil
              AND NOT EXISTS (
                  SELECT 1 FROM EstadoEvaluacionFichaJpaEntity s2
                  WHERE s2.evaluacionFichaPerfil.id = e.id
                    AND (s2.fechaActualizacion > s.fechaActualizacion
                         OR (s2.fechaActualizacion = s.fechaActualizacion AND s2.id > s.id)))
            GROUP BY s.estadoEvaluacion.id
            """)
    List<ConteoEvaluacionesPorEstadoEntity> contarEvaluacionesDeFichaPorEstadoEvaluacionActual(@Param("fichaPerfil") UUID fichaPerfil);
}
