package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.entity.CriterioItemCualitativoAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface CriterioItemCualitativoAsesorCommandRepository extends JpaRepository<CriterioItemCualitativoAsesorJpaEntity, UUID> {

    @Query("select e.id from CriterioItemCualitativoAsesorJpaEntity e where e.id in :ids")
    Set<UUID> findIdsByIdIn(@Param("ids") Set<UUID> ids);
}
