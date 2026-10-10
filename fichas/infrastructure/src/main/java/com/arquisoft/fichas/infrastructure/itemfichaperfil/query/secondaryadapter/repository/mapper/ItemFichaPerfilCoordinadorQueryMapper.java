package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.ItemFichaPerfilCoordinadorJpaQueryEntity;

public final class ItemFichaPerfilCoordinadorQueryMapper {

    private ItemFichaPerfilCoordinadorQueryMapper() {}

    public static ItemFichaPerfilReadModel toReadModel(ItemFichaPerfilCoordinadorJpaQueryEntity entity) {
        return new ItemFichaPerfilReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getTipoItemId(),
                entity.getTipoItemNombre(),
                entity.getContenido());
    }
}
