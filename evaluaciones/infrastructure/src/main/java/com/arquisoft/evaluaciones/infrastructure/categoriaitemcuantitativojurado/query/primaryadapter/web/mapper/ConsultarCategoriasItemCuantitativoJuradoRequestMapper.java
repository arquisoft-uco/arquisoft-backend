package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model.ConsultarCategoriasItemCuantitativoJuradoQuery;

public final class ConsultarCategoriasItemCuantitativoJuradoRequestMapper {

    private ConsultarCategoriasItemCuantitativoJuradoRequestMapper() {}

    public static ConsultarCategoriasItemCuantitativoJuradoQuery toQuery(String nombre) {
        return ConsultarCategoriasItemCuantitativoJuradoQuery.crear(nombre);
    }
}
