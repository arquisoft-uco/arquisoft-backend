package com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web.dto.EstadoUsuarioResponseDTO;

public final class EstadoUsuarioResponseMapper {

    private EstadoUsuarioResponseMapper() {}

    public static EstadoUsuarioResponseDTO toResponse(EstadoUsuarioReadModel readModel) {
        return new EstadoUsuarioResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
