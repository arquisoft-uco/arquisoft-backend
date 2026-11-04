package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.IdentidadUsuarioResponseDTO;

public final class IdentidadUsuarioResponseMapper {

    private IdentidadUsuarioResponseMapper() {}

    public static IdentidadUsuarioResponseDTO toResponse(IdentidadUsuarioReadModel readModel) {
        return new IdentidadUsuarioResponseDTO(readModel.nombres(), readModel.apellidos());
    }
}
