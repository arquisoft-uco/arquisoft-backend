package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.command.secondaryadapter.entity.ObservacionItemAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservacionItemAsesorCommandRepository
        extends JpaRepository<ObservacionItemAsesorJpaEntity, UUID> {

    boolean existsByEvaluacionCuantitativaAsesorIdAndDescripcion(UUID evaluacionCuantitativaAsesorId, String descripcion);
}
