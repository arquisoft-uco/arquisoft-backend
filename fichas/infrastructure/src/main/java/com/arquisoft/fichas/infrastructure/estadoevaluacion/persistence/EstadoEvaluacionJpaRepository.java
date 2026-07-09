package com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoEvaluacionJpaRepository extends JpaRepository<EstadoEvaluacionJpaEntity, String> {
}
