package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.ItemFichaPerfilJpaQueryEntity;

public final class ItemFichaPerfilQueryMapper {

    private ItemFichaPerfilQueryMapper() {}

    public static ItemFichaPerfilReadModel toReadModel(ItemFichaPerfilJpaQueryEntity entity) {
        return new ItemFichaPerfilReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getTipoItemId(),
                entity.getTipoItemNombre(),
                entity.getContenido());
    }
}
