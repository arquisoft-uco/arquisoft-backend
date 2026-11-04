package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository.CriterioItemCualitativoAsesorJpaQueryEntity;

public final class CriterioItemCualitativoAsesorQueryMapper {

    private CriterioItemCualitativoAsesorQueryMapper() {}

    public static CriterioItemCualitativoAsesorReadModel toReadModel(CriterioItemCualitativoAsesorJpaQueryEntity entity) {
        return new CriterioItemCualitativoAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
