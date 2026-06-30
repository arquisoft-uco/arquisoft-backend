package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemJpaEntity;

public final class ItemFichaPerfilMapper {

    private ItemFichaPerfilMapper() {}

    public static ItemFichaPerfilJpaEntity toJpaEntity(
            ItemFichaPerfilAggregate aggregate,
            TipoItemJpaEntity tipoItemRef) {
        return ItemFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .tipoItem(tipoItemRef)
                .contenido(aggregate.getContenido())
                .build();
    }

    public static ItemFichaPerfilAggregate toDomain(ItemFichaPerfilJpaEntity entity) {
        TipoItem tipoItem = TipoItem.valueOf(entity.getTipoItem().getId());
        return ItemFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                tipoItem,
                entity.getContenido()
        );
    }
}
