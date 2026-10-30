package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.dto.ItemCuantitativoAsesorResponseDTO;

public final class ItemCuantitativoAsesorResponseMapper {

    private ItemCuantitativoAsesorResponseMapper() {}

    public static ItemCuantitativoAsesorResponseDTO toResponse(ItemCuantitativoAsesorReadModel readModel) {
        return new ItemCuantitativoAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion(),
                readModel.categoriaId(),
                readModel.valor());
    }
}
