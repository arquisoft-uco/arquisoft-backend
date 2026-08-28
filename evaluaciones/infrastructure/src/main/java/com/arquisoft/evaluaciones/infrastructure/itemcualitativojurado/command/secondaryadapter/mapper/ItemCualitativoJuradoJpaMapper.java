package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;

public final class ItemCualitativoJuradoJpaMapper {

    private ItemCualitativoJuradoJpaMapper() {}

    public static ItemCualitativoJuradoEntity toEntity(
            ItemCualitativoJuradoJpaEntity jpaEntity) {
        return new ItemCualitativoJuradoEntity(
                jpaEntity.getId(), jpaEntity.getNombre(), jpaEntity.getDescripcion());
    }

    public static ItemCualitativoJuradoJpaEntity toJpaEntity(
            ItemCualitativoJuradoEntity entity) {
        return ItemCualitativoJuradoJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }
}
