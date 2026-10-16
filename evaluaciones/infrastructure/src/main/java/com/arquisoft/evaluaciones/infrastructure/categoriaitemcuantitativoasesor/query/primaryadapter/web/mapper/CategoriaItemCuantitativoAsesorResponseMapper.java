package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.dto.CategoriaItemCuantitativoAsesorResponseDTO;

public final class CategoriaItemCuantitativoAsesorResponseMapper {

    private CategoriaItemCuantitativoAsesorResponseMapper() {}

    public static CategoriaItemCuantitativoAsesorResponseDTO toResponse(
            CategoriaItemCuantitativoAsesorReadModel readModel) {
        return new CategoriaItemCuantitativoAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
