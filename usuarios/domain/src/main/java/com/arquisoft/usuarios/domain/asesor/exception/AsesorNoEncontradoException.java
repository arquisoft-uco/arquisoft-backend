package com.arquisoft.usuarios.domain.asesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorKey;

import java.util.UUID;

public final class AsesorNoEncontradoException extends DomainException {

    public AsesorNoEncontradoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Asesor.ASESOR_NO_ENCONTRADO,
                Mensajes.formatear(RemoverAsesorKey.ERROR_NO_ENCONTRADO, nombre),
                Mensajes.formatear(RemoverAsesorKey.ERROR_NO_ENCONTRADO_DETALLE, usuario)
        ));
    }
}
