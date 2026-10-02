package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.secondaryadapter.entity.ObservacionEvaluacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservacionEvaluacionCommandRepository extends JpaRepository<ObservacionEvaluacionJpaEntity, UUID> {

    boolean existsByEvaluacionFichaPerfilIdAndObservacion(UUID evaluacionFichaPerfilId, String observacion);
}
