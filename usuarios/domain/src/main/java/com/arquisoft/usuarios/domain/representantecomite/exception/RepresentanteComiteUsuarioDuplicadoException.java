package com.arquisoft.usuarios.domain.representantecomite.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;

import java.util.UUID;

public final class RepresentanteComiteUsuarioDuplicadoException extends DomainException {

    public RepresentanteComiteUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarRepresentanteComiteKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.RepresentanteComite.USUARIO_DUPLICADO
        );
    }
}
