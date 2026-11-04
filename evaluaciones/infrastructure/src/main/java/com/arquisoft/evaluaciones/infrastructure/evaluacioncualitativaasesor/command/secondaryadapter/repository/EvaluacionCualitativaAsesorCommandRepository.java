package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.entity.EvaluacionCualitativaAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionCualitativaAsesorCommandRepository
        extends JpaRepository<EvaluacionCualitativaAsesorJpaEntity, UUID> {

    @Query("select e.item from EvaluacionCualitativaAsesorJpaEntity e "
            + "where e.evaluacionAsesor = :evaluacionAsesor and e.item in :items")
    Set<UUID> findItemsRegistrados(
            @Param("evaluacionAsesor") UUID evaluacionAsesor, @Param("items") Set<UUID> items);
}
