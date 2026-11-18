package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.projection.ValorMaximoItemCuantitativoAsesorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ItemCuantitativoAsesorCommandRepository extends JpaRepository<ItemCuantitativoAsesorJpaEntity, UUID> {

    @Query("select e.id as id, e.valor as valor from ItemCuantitativoAsesorJpaEntity e where e.id in :ids")
    List<ValorMaximoItemCuantitativoAsesorProjection> findValoresMaximosByIdIn(@Param("ids") Set<UUID> ids);

    boolean existsByNombreIgnoreCaseAndCategoria(String nombre, UUID categoria);

    @Modifying(clearAutomatically = true)
    @Query("update ItemCuantitativoAsesorJpaEntity item set item.descripcion = :descripcion where item.id = :id")
    int actualizarDescripcion(@Param("id") UUID id, @Param("descripcion") String descripcion);
}
