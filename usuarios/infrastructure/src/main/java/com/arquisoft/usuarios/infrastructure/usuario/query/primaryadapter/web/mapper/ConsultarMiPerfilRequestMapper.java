package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;

import java.util.UUID;

public final class ConsultarMiPerfilRequestMapper {

    private ConsultarMiPerfilRequestMapper() {}

    public static ConsultarMiPerfilQuery toQuery(UUID usuario) {
        return ConsultarMiPerfilQuery.crear(usuario);
    }
}
