package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web.dto.ItemCuantitativoJuradoResponseDTO;

public final class ItemCuantitativoJuradoResponseMapper {

    private ItemCuantitativoJuradoResponseMapper() {}

    public static ItemCuantitativoJuradoResponseDTO toResponse(ItemCuantitativoJuradoReadModel readModel) {
        return new ItemCuantitativoJuradoResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion(),
                readModel.categoriaId(),
                readModel.valor());
    }
}
