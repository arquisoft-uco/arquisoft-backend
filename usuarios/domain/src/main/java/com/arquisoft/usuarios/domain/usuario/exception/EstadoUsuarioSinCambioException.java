package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.CambiarEstadoUsuarioKey;

import java.util.UUID;

public final class EstadoUsuarioSinCambioException extends DomainException {

    public EstadoUsuarioSinCambioException(UUID usuario, String estado) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.ESTADO_SIN_CAMBIO,
                Mensajes.formatear(CambiarEstadoUsuarioKey.ERROR_ESTADO_SIN_CAMBIO, estado),
                Mensajes.formatear(CambiarEstadoUsuarioKey.ERROR_ESTADO_SIN_CAMBIO_DETALLE, usuario, estado)
        ));
    }
}
