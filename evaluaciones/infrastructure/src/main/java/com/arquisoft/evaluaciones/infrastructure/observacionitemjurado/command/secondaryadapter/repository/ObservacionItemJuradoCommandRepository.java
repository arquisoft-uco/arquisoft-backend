package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObservacionItemJuradoCommandRepository
        extends JpaRepository<ObservacionItemJuradoJpaEntity, UUID> {

    boolean existsByEvaluacionCuantitativaJuradoIdAndDescripcion(UUID evaluacionCuantitativaJuradoId, String descripcion);
}
