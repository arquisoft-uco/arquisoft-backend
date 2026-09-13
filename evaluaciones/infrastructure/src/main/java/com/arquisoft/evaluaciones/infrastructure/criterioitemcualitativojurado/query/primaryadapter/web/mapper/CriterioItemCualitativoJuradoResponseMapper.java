package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web.dto.CriterioItemCualitativoJuradoResponseDTO;

public final class CriterioItemCualitativoJuradoResponseMapper {

    private CriterioItemCualitativoJuradoResponseMapper() {}

    public static CriterioItemCualitativoJuradoResponseDTO toResponse(CriterioItemCualitativoJuradoReadModel readModel) {
        return new CriterioItemCualitativoJuradoResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
