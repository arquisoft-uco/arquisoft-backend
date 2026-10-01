package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;

public final class ItemFichaPerfilMapper {

    private ItemFichaPerfilMapper() {}

    public static ItemFichaPerfilEntity toEntity(ItemFichaPerfilDomain aggregate) {
        return new ItemFichaPerfilEntity(
                aggregate.getId(),
                aggregate.getFichaPerfilId(),
                aggregate.getTipoItem().getId(),
                aggregate.getContenido());
    }
}
