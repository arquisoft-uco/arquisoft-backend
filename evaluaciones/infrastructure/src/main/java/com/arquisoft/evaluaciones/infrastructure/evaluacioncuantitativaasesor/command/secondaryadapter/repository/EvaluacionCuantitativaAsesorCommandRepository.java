package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionCuantitativaAsesorCommandRepository
        extends JpaRepository<EvaluacionCuantitativaAsesorJpaEntity, UUID> {

    @Query("select e.item from EvaluacionCuantitativaAsesorJpaEntity e "
            + "where e.evaluacionAsesor = :evaluacionAsesor and e.item in :items")
    Set<UUID> findItemsRegistrados(
            @Param("evaluacionAsesor") UUID evaluacionAsesor, @Param("items") Set<UUID> items);
}
