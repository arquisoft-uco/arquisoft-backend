package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemEntity;

public final class ItemFichaPerfilMapper {

    private ItemFichaPerfilMapper() {}

    public static ItemFichaPerfilEntity toEntity(
            ItemFichaPerfilDomain aggregate,
            TipoItemEntity tipoItemRef) {
        return ItemFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .tipoItem(tipoItemRef)
                .contenido(aggregate.getContenido())
                .build();
    }
}
