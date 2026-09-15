package com.arquisoft.usuarios.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;

import java.util.UUID;

public final class AsesorUsuarioDuplicadoException extends DomainException {

    public AsesorUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarAsesorKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.Asesor.USUARIO_DUPLICADO
        );
    }
}
