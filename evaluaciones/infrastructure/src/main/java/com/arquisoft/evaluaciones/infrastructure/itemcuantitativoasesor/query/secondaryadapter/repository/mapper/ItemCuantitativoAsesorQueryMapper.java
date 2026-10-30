package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository.ItemCuantitativoAsesorJpaQueryEntity;

public final class ItemCuantitativoAsesorQueryMapper {

    private ItemCuantitativoAsesorQueryMapper() {}

    public static ItemCuantitativoAsesorReadModel toReadModel(ItemCuantitativoAsesorJpaQueryEntity entity) {
        return new ItemCuantitativoAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getCategoriaId(),
                entity.getValor());
    }
}
