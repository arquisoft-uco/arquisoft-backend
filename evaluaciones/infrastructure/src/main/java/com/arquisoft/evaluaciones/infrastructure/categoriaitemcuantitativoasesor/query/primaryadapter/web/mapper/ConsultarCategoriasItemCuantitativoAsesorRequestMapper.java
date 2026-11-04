package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model.ConsultarCategoriasItemCuantitativoAsesorQuery;

public final class ConsultarCategoriasItemCuantitativoAsesorRequestMapper {

    private ConsultarCategoriasItemCuantitativoAsesorRequestMapper() {}

    public static ConsultarCategoriasItemCuantitativoAsesorQuery toQuery(String nombre) {
        return ConsultarCategoriasItemCuantitativoAsesorQuery.crear(nombre);
    }
}
