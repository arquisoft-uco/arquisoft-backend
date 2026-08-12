package com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoevaluacion.command.secondaryport.entity.EstadoEvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoEvaluacionCommandRepository extends JpaRepository<EstadoEvaluacionEntity, String> {
}
