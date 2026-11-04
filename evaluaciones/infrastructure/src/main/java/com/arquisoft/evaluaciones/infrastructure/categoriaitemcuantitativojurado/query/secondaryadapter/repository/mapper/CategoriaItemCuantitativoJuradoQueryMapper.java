package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository.CategoriaItemCuantitativoJuradoJpaQueryEntity;

public final class CategoriaItemCuantitativoJuradoQueryMapper {

    private CategoriaItemCuantitativoJuradoQueryMapper() {}

    public static CategoriaItemCuantitativoJuradoReadModel toReadModel(
            CategoriaItemCuantitativoJuradoJpaQueryEntity entity) {
        return new CategoriaItemCuantitativoJuradoReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
