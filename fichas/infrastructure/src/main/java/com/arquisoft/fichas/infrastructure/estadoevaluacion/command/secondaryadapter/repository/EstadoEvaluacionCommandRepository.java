package com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoEvaluacionCommandRepository extends JpaRepository<EstadoEvaluacionJpaEntity, String> {
}
