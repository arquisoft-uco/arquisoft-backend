package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EstadoEvaluacionFichaRepository
        extends JpaRepository<EstadoEvaluacionFichaEntity, UUID> {

    boolean existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(
            UUID evaluacionFichaPerfilId,
            String estadoEvaluacionId);

    long countByEvaluacionFichaPerfilId(UUID evaluacionFichaPerfilId);

    @Query("SELECT e FROM EstadoEvaluacionFichaEntity e "
            + "WHERE e.evaluacionFichaPerfil.id = :evaluacionId "
            + "ORDER BY e.fechaActualizacion DESC LIMIT 1")
    Optional<EstadoEvaluacionFichaEntity> findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(
            @Param("evaluacionId") UUID evaluacionId);
}
