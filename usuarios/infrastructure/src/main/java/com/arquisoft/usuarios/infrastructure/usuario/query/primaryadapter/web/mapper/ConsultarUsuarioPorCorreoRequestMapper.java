package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarUsuarioPorCorreoQuery;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.ConsultarUsuarioPorCorreoRequestDTO;

public final class ConsultarUsuarioPorCorreoRequestMapper {

    private ConsultarUsuarioPorCorreoRequestMapper() {}

    public static ConsultarUsuarioPorCorreoQuery toQuery(ConsultarUsuarioPorCorreoRequestDTO request) {
        return ConsultarUsuarioPorCorreoQuery.crear(request.correo());
    }
}
