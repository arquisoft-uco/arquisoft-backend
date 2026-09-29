package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EvaluacionCommandRepository extends JpaRepository<EvaluacionJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("update EvaluacionJpaEntity evaluacion "
            + "set evaluacion.estadoEvaluacion = :estado where evaluacion.id = :id")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);
}
