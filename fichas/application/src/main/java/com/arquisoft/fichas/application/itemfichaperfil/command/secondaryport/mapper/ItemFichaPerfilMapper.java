package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.application.tipoitem.command.secondaryport.entity.TipoItemEntity;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;

public final class ItemFichaPerfilMapper {

    private ItemFichaPerfilMapper() {}

    public static ItemFichaPerfilEntity toEntity(ItemFichaPerfilDomain aggregate) {
        return ItemFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .tipoItem(TipoItemEntity.builder()
                        .id(aggregate.getTipoItem().getId())
                        .build())
                .contenido(aggregate.getContenido())
                .build();
    }
}
