package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository.ItemCuantitativoJuradoJpaQueryEntity;

public final class ItemCuantitativoJuradoQueryMapper {

    private ItemCuantitativoJuradoQueryMapper() {}

    public static ItemCuantitativoJuradoReadModel toReadModel(ItemCuantitativoJuradoJpaQueryEntity entity) {
        return new ItemCuantitativoJuradoReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getCategoriaId(),
                entity.getValor());
    }
}
