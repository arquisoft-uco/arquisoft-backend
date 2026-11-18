package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoJuradoCommandRepository
        extends JpaRepository<ItemCualitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCaseAndEliminadoEnIsNull(String nombre);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCualitativoJuradoJpaEntity item "
            + "set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);

    @Query("select item.id from ItemCualitativoJuradoJpaEntity item where item.id in :ids and item.eliminadoEn is null")
    Set<UUID> findIdsByIdIn(@Param("ids") Set<UUID> ids);

    boolean existsByIdAndEliminadoEnIsNull(UUID id);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCualitativoJuradoJpaEntity item set item.eliminadoEn = :eliminadoEn where item.id = :id")
    int marcarEliminado(@Param("id") UUID id, @Param("eliminadoEn") Instant eliminadoEn);
}
