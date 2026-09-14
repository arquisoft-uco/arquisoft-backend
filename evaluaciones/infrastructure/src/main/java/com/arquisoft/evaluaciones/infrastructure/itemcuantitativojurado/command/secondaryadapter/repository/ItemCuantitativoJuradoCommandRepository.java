package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemCuantitativoJuradoCommandRepository
        extends JpaRepository<ItemCuantitativoJuradoJpaEntity, UUID> {

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);

    @Query(
            value = "select exists(select 1 from categoria_item_cuantitativo_jurado "
                    + "where id = :categoriaId)",
            nativeQuery = true)
    boolean existsCategoriaById(@Param("categoriaId") UUID categoriaId);
}
