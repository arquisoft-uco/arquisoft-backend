package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;

public final class ItemCuantitativoJuradoJpaMapper {

    private ItemCuantitativoJuradoJpaMapper() {}

    public static ItemCuantitativoJuradoEntity toEntity(
            ItemCuantitativoJuradoJpaEntity jpaEntity) {
        return new ItemCuantitativoJuradoEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion(),
                jpaEntity.getCategoriaId(),
                jpaEntity.getValor());
    }

    public static ItemCuantitativoJuradoJpaEntity toJpaEntity(
            ItemCuantitativoJuradoEntity entity) {
        return ItemCuantitativoJuradoJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .categoriaId(entity.categoriaId())
                .valor(entity.valor())
                .build();
    }
}
