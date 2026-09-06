package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemCualitativoJuradoCommandRepository
        extends JpaRepository<ItemCualitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCualitativoJuradoJpaEntity item "
            + "set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
