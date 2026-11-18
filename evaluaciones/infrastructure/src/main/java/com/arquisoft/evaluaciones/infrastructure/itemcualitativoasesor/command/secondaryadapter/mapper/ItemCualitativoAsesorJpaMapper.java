package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.entity.ItemCualitativoAsesorJpaEntity;

public final class ItemCualitativoAsesorJpaMapper {

    private ItemCualitativoAsesorJpaMapper() {}

    public static ItemCualitativoAsesorJpaEntity toJpaEntity(ItemCualitativoAsesorEntity entity) {
        return ItemCualitativoAsesorJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }
}
