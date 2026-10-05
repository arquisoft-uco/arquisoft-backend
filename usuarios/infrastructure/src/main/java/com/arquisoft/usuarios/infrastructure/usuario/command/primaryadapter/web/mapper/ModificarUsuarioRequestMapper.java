package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.ModificarUsuarioCommand;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.ModificarUsuarioRequestDTO;

public final class ModificarUsuarioRequestMapper {

    private ModificarUsuarioRequestMapper() {}

    public static ModificarUsuarioCommand toCommand(String usuarioId, ModificarUsuarioRequestDTO request) {
        return ModificarUsuarioCommand.crear(
                usuarioId,
                new ModificarUsuarioCommand.DatosModificarUsuario(
                        request.identificador(),
                        request.nombre(),
                        request.email(),
                        request.contacto(),
                        request.nombres(),
                        request.apellidos()),
                request.roles());
    }
}
