package com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.tipoitem.command.secondaryport.entity.TipoItemEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.entity.TipoItemJpaEntity;

public final class TipoItemJpaMapper {

    private TipoItemJpaMapper() {}

    public static TipoItemEntity toEntity(TipoItemJpaEntity jpaEntity) {
        return new TipoItemEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion());
    }

    public static TipoItemJpaEntity toJpaEntity(TipoItemEntity entity) {
        return TipoItemJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }

    public static TipoItemJpaEntity toReferencia(String id) {
        return TipoItemJpaEntity.builder().id(id).build();
    }
}
