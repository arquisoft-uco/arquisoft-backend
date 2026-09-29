package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface EvaluacionCualitativaJuradoCommandRepository
        extends JpaRepository<EvaluacionCualitativaJuradoJpaEntity, UUID> {

    @Query("select e.item from EvaluacionCualitativaJuradoJpaEntity e "
            + "where e.evaluacionJurado = :evaluacionJurado and e.item in :items")
    Set<UUID> findItemsRegistrados(
            @Param("evaluacionJurado") UUID evaluacionJurado, @Param("items") Set<UUID> items);
}
