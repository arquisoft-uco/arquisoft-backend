package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.shared.util.UtilTexto;

public final class RegistrarUsuarioMapper {

    private static final String SEPARADOR_NOMBRE = " ";

    private RegistrarUsuarioMapper() {}

    public static RegistroUsuarioDomain toDomain(RegistrarUsuarioCommand command) {
        var nombreCompleto = UtilTexto.aplicarTrim(command.nombres())
                + SEPARADOR_NOMBRE
                + UtilTexto.aplicarTrim(command.apellidos());

        return RegistroUsuarioDomain.crear(
                command.identificador(), nombreCompleto, command.email(), command.contacto(),
                command.nombres(), command.apellidos(), command.roles());
    }
}
