package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UsuarioRolesVigentesException extends DomainException {

    private static final String SEPARADOR = ", ";

    public UsuarioRolesVigentesException(UUID usuario, String nombre, List<String> roles) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.ROLES_VIGENTES,
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ROLES_VIGENTES, nombre, roles.stream()
                        .map(UsuariosRoles::etiqueta)
                        .collect(Collectors.joining(SEPARADOR))),
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ROLES_VIGENTES_DETALLE, usuario,
                        String.join(SEPARADOR, roles))
        ));
    }
}
