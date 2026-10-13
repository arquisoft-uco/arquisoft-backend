package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.criteria.CategoriaItemCuantitativoJuradoCriteria;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model.ConsultarCategoriasItemCuantitativoJuradoQuery;

public final class ConsultarCategoriasItemCuantitativoJuradoMapper {

    private ConsultarCategoriasItemCuantitativoJuradoMapper() {}

    public static CategoriaItemCuantitativoJuradoCriteria toCriteria(
            ConsultarCategoriasItemCuantitativoJuradoQuery query) {
        return new CategoriaItemCuantitativoJuradoCriteria(query.nombre());
    }
}
