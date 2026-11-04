package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarIdentidadUsuarioQuery;

public final class ConsultarIdentidadUsuarioMapper {

    private ConsultarIdentidadUsuarioMapper() {}

    public static IdentidadUsuarioCriteria toCriteria(ConsultarIdentidadUsuarioQuery query) {
        return new IdentidadUsuarioCriteria(query.usuario());
    }
}
