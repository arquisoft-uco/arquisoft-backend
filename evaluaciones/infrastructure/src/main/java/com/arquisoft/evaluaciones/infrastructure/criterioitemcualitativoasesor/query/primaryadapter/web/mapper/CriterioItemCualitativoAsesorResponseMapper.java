package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.dto.CriterioItemCualitativoAsesorResponseDTO;

public final class CriterioItemCualitativoAsesorResponseMapper {

    private CriterioItemCualitativoAsesorResponseMapper() {}

    public static CriterioItemCualitativoAsesorResponseDTO toResponse(CriterioItemCualitativoAsesorReadModel readModel) {
        return new CriterioItemCualitativoAsesorResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
