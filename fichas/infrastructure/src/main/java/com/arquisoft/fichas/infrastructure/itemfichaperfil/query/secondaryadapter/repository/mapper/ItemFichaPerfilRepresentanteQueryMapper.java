package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.ItemFichaPerfilRepresentanteJpaQueryEntity;

public final class ItemFichaPerfilRepresentanteQueryMapper {

    private ItemFichaPerfilRepresentanteQueryMapper() {}

    public static ItemFichaPerfilReadModel toReadModel(ItemFichaPerfilRepresentanteJpaQueryEntity entity) {
        return new ItemFichaPerfilReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getTipoItemId(),
                entity.getTipoItemNombre(),
                entity.getContenido());
    }
}
