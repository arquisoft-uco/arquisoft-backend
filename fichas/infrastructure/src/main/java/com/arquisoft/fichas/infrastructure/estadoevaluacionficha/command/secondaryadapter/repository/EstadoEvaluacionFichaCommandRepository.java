package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEvaluacionFichaCommandRepository
        extends JpaRepository<EstadoEvaluacionFichaJpaEntity, UUID> {

    boolean existsByEvaluacionFichaPerfilAndEstadoEvaluacion(
            UUID evaluacionFichaPerfil,
            String estadoEvaluacion);

    long countByEvaluacionFichaPerfil(UUID evaluacionFichaPerfil);

    @Query("SELECT e FROM EstadoEvaluacionFichaJpaEntity e "
            + "WHERE e.evaluacionFichaPerfil = :evaluacionId "
            + "ORDER BY e.fechaActualizacion DESC LIMIT 1")
    Optional<EstadoEvaluacionFichaJpaEntity> findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(
            @Param("evaluacionId") UUID evaluacionId);
}
