package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.dto.CategoriaItemCuantitativoJuradoResponseDTO;

public final class CategoriaItemCuantitativoJuradoResponseMapper {

    private CategoriaItemCuantitativoJuradoResponseMapper() {}

    public static CategoriaItemCuantitativoJuradoResponseDTO toResponse(
            CategoriaItemCuantitativoJuradoReadModel readModel) {
        return new CategoriaItemCuantitativoJuradoResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
