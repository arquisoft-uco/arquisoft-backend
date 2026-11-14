package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.UsuarioPorCorreoResponseDTO;

public final class UsuarioPorCorreoResponseMapper {

    private UsuarioPorCorreoResponseMapper() {}

    public static UsuarioPorCorreoResponseDTO toResponse(UsuarioPorCorreoReadModel readModel) {
        return new UsuarioPorCorreoResponseDTO(readModel.id());
    }
}
