package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemCuantitativoJuradoCommandRepository
        extends JpaRepository<ItemCuantitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCuantitativoJuradoJpaEntity item set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
