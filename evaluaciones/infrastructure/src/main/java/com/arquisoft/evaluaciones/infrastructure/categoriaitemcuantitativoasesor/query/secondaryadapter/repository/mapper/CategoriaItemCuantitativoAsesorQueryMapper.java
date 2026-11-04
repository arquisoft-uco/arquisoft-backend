package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository.CategoriaItemCuantitativoAsesorJpaQueryEntity;

public final class CategoriaItemCuantitativoAsesorQueryMapper {

    private CategoriaItemCuantitativoAsesorQueryMapper() {}

    public static CategoriaItemCuantitativoAsesorReadModel toReadModel(
            CategoriaItemCuantitativoAsesorJpaQueryEntity entity) {
        return new CategoriaItemCuantitativoAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
