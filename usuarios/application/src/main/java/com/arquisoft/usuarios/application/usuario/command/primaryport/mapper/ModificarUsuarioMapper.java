package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.ModificarUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;

public final class ModificarUsuarioMapper {

    private ModificarUsuarioMapper() {}

    public static ModificacionUsuarioDomain toDomain(ModificarUsuarioCommand command) {
        return ModificacionUsuarioDomain.crear(
                command.usuario(),
                new ModificacionUsuarioDomain.DatosModificacionUsuario(
                        command.identificador(),
                        command.nombre(),
                        command.email(),
                        command.contacto(),
                        command.nombres(),
                        command.apellidos()),
                command.roles());
    }
}
