package com.arquisoft.usuarios.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorKey;

import java.util.UUID;

public final class AsesorNoEncontradoException extends DomainException {

    public AsesorNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverAsesorKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Asesor.ASESOR_NO_ENCONTRADO
        );
    }
}
