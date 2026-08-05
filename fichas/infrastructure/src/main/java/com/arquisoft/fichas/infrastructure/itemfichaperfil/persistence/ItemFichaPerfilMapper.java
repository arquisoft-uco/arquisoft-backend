package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
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

    public static ItemFichaPerfilDomain toDomain(ItemFichaPerfilEntity entity) {
        TipoItem tipoItem = TipoItem.valueOf(entity.getTipoItem().getId());
        return ItemFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                tipoItem,
                entity.getContenido()
        );
    }
}
