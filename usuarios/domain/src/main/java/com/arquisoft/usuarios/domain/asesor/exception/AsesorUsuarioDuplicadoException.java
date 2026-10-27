package com.arquisoft.usuarios.domain.asesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;

import java.util.UUID;

public final class AsesorUsuarioDuplicadoException extends DomainException {

    public AsesorUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Asesor.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarAsesorKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarAsesorKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
