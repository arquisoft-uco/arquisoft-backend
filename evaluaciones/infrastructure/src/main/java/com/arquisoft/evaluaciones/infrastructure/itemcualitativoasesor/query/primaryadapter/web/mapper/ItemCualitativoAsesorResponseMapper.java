package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.dto.ItemCualitativoAsesorResponseDTO;

public final class ItemCualitativoAsesorResponseMapper {

    private ItemCualitativoAsesorResponseMapper() {}

    public static ItemCualitativoAsesorResponseDTO toResponse(ItemCualitativoAsesorReadModel readModel) {
        return new ItemCualitativoAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
