package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.PerfilUsuarioResponseDTO;
import com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto.PerfilUsuarioResponseDTO.ElementoPerfilDTO;

public final class PerfilUsuarioResponseMapper {

    private PerfilUsuarioResponseMapper() {}

    public static PerfilUsuarioResponseDTO toResponse(PerfilUsuarioReadModel readModel) {
        return new PerfilUsuarioResponseDTO(
                readModel.id(),
                readModel.identificador(),
                readModel.nombre(),
                readModel.email(),
                readModel.contacto(),
                new ElementoPerfilDTO(readModel.estado().id(), readModel.estado().nombre()),
                readModel.roles().stream().map(PerfilUsuarioResponseMapper::toRol).toList());
    }

    private static ElementoPerfilDTO toRol(RolPerfilReadModel rol) {
        return new ElementoPerfilDTO(rol.id(), rol.nombre());
    }
}
