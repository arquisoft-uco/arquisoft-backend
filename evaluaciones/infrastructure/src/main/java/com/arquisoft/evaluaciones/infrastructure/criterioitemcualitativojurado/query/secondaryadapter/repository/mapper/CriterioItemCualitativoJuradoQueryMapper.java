package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository.CriterioItemCualitativoJuradoJpaQueryEntity;

public final class CriterioItemCualitativoJuradoQueryMapper {

    private CriterioItemCualitativoJuradoQueryMapper() {}

    public static CriterioItemCualitativoJuradoReadModel toReadModel(CriterioItemCualitativoJuradoJpaQueryEntity entity) {
        return new CriterioItemCualitativoJuradoReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
