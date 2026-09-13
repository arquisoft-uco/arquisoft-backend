package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.RegistrarUsuarioRequestDTO;

public final class RegistrarUsuarioRequestMapper {

    private RegistrarUsuarioRequestMapper() {}

    public static RegistrarUsuarioCommand toCommand(RegistrarUsuarioRequestDTO dto) {
        return RegistrarUsuarioCommand.crear(
                dto.identificador(),
                dto.nombres(),
                dto.apellidos(),
                dto.email(),
                dto.contacto(),
                dto.roles());
    }
}
