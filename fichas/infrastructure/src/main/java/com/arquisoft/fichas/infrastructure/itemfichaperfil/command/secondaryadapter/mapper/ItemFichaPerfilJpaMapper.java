package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.entity.ItemFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.tipoitem.command.secondaryadapter.mapper.TipoItemJpaMapper;

public final class ItemFichaPerfilJpaMapper {

    private ItemFichaPerfilJpaMapper() {}

    public static ItemFichaPerfilEntity toEntity(ItemFichaPerfilJpaEntity jpaEntity) {
        return new ItemFichaPerfilEntity(
                jpaEntity.getId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getTipoItem().getId(),
                jpaEntity.getContenido());
    }

    public static ItemFichaPerfilJpaEntity toJpaEntity(ItemFichaPerfilEntity entity) {
        return ItemFichaPerfilJpaEntity.builder()
                .id(entity.id())
                .fichaPerfilId(entity.fichaPerfilId())
                .tipoItem(TipoItemJpaMapper.toReferencia(entity.tipoItem()))
                .contenido(entity.contenido())
                .build();
    }
}
