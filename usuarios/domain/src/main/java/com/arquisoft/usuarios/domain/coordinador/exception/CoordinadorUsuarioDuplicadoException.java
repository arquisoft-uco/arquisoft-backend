package com.arquisoft.usuarios.domain.coordinador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;

import java.util.UUID;

public final class CoordinadorUsuarioDuplicadoException extends DomainException {

    public CoordinadorUsuarioDuplicadoException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Coordinador.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarCoordinadorKey.ERROR_USUARIO_DUPLICADO),
                Mensajes.formatear(AgregarCoordinadorKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
