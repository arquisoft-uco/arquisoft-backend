package com.arquisoft.usuarios.domain.representantecomite.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;

import java.util.UUID;

public final class RepresentanteComiteUsuarioDuplicadoException extends DomainException {

    public RepresentanteComiteUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.RepresentanteComite.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarRepresentanteComiteKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarRepresentanteComiteKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
