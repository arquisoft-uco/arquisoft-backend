package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoAsesorCommandRepository extends JpaRepository<ItemCualitativoAsesorJpaEntity, UUID> {

    @Query("select e.id from ItemCualitativoAsesorJpaEntity e where e.id in :ids and e.eliminadoEn is null")
    Set<UUID> findIdsByIdIn(@Param("ids") Set<UUID> ids);

    boolean existsByNombreIgnoreCaseAndEliminadoEnIsNull(String nombre);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCualitativoAsesorJpaEntity item set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);

    boolean existsByIdAndEliminadoEnIsNull(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCualitativoAsesorJpaEntity item set item.eliminadoEn = :eliminadoEn where item.id = :id")
    int marcarEliminado(@Param("id") UUID id, @Param("eliminadoEn") Instant eliminadoEn);
}
