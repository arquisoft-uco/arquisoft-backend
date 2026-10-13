package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.entity.CriterioItemCualitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface CriterioItemCualitativoJuradoCommandRepository
        extends JpaRepository<CriterioItemCualitativoJuradoJpaEntity, UUID> {

    @Query("select criterio.id from CriterioItemCualitativoJuradoJpaEntity criterio where criterio.id in :ids")
    Set<UUID> findIdsByIdIn(@Param("ids") Set<UUID> ids);
}
