package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web.dto.ItemCualitativoJuradoResponseDTO;

public final class ItemCualitativoJuradoResponseMapper {

    private ItemCualitativoJuradoResponseMapper() {}

    public static ItemCualitativoJuradoResponseDTO toResponse(ItemCualitativoJuradoReadModel readModel) {
        return new ItemCualitativoJuradoResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
