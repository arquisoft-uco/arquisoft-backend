package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoAsesorCommandRepository extends JpaRepository<ItemCualitativoAsesorJpaEntity, UUID> {

    @Query("select e.id from ItemCualitativoAsesorJpaEntity e where e.id in :ids")
    Set<UUID> findIdsByIdIn(@Param("ids") Set<UUID> ids);
}
