package com.arquisoft.usuarios.domain.jurado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;

import java.util.UUID;

public final class JuradoUsuarioDuplicadoException extends DomainException {

    public JuradoUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Jurado.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarJuradoKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarJuradoKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
