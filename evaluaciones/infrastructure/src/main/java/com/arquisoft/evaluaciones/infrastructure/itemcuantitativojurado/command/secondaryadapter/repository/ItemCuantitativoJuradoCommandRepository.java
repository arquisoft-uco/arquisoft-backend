package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.projection.ValorMaximoItemCuantitativoJuradoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ItemCuantitativoJuradoCommandRepository
        extends JpaRepository<ItemCuantitativoJuradoJpaEntity, UUID> {

    @Query("select e.id as id, e.valor as valor from ItemCuantitativoJuradoJpaEntity e where e.id in :ids")
    List<ValorMaximoItemCuantitativoJuradoProjection> findValoresMaximosByIdIn(@Param("ids") Set<UUID> ids);

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, UUID categoriaId);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCuantitativoJuradoJpaEntity item set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
