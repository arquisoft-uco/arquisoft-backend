package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarIdentidadUsuarioQuery;

import java.util.UUID;

public final class ConsultarIdentidadUsuarioRequestMapper {

    private ConsultarIdentidadUsuarioRequestMapper() {}

    public static ConsultarIdentidadUsuarioQuery toQuery(UUID usuarioId) {
        return ConsultarIdentidadUsuarioQuery.crear(usuarioId);
    }
}
