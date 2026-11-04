package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.criteria.CategoriaItemCuantitativoAsesorCriteria;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model.ConsultarCategoriasItemCuantitativoAsesorQuery;

public final class ConsultarCategoriasItemCuantitativoAsesorMapper {

    private ConsultarCategoriasItemCuantitativoAsesorMapper() {}

    public static CategoriaItemCuantitativoAsesorCriteria toCriteria(
            ConsultarCategoriasItemCuantitativoAsesorQuery query) {
        return new CategoriaItemCuantitativoAsesorCriteria(query.nombre());
    }
}
