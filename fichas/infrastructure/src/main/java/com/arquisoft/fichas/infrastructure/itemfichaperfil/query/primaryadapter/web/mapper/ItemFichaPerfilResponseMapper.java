package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.dto.ItemFichaPerfilResponseDTO;

public final class ItemFichaPerfilResponseMapper {

    private ItemFichaPerfilResponseMapper() {}

    public static ItemFichaPerfilResponseDTO toResponse(ItemFichaPerfilReadModel readModel) {
        return new ItemFichaPerfilResponseDTO(
                readModel.id(),
                readModel.fichaPerfilId(),
                readModel.tipoItem(),
                readModel.tipoItemNombre(),
                readModel.contenido());
    }
}
