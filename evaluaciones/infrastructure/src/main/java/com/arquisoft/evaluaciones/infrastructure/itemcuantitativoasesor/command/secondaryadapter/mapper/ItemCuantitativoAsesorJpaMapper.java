package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.entity.ItemCuantitativoAsesorJpaEntity;

public final class ItemCuantitativoAsesorJpaMapper {

    private ItemCuantitativoAsesorJpaMapper() {}

    public static ItemCuantitativoAsesorJpaEntity toJpaEntity(ItemCuantitativoAsesorEntity entity) {
        return ItemCuantitativoAsesorJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .categoria(entity.categoriaId())
                .valor(entity.valor())
                .build();
    }
}
