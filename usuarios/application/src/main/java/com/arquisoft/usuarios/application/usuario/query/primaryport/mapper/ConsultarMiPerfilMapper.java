package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;

public final class ConsultarMiPerfilMapper {

    private ConsultarMiPerfilMapper() {}

    public static PerfilUsuarioCriteria toCriteria(ConsultarMiPerfilQuery query) {
        return new PerfilUsuarioCriteria(query.usuario());
    }
}
