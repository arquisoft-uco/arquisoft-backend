package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository.ItemCualitativoAsesorJpaQueryEntity;

public final class ItemCualitativoAsesorQueryMapper {

    private ItemCualitativoAsesorQueryMapper() {}

    public static ItemCualitativoAsesorReadModel toReadModel(ItemCualitativoAsesorJpaQueryEntity entity) {
        return new ItemCualitativoAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
