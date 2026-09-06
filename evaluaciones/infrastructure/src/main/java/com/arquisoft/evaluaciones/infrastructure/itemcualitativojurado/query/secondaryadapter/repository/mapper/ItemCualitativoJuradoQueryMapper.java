package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository.ItemCualitativoJuradoJpaQueryEntity;

public final class ItemCualitativoJuradoQueryMapper {

    private ItemCualitativoJuradoQueryMapper() {}

    public static ItemCualitativoJuradoReadModel toReadModel(ItemCualitativoJuradoJpaQueryEntity entity) {
        return new ItemCualitativoJuradoReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
