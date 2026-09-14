package com.arquisoft.usuarios.domain.asesorficha.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;

import java.util.UUID;

public final class AsesorFichaUsuarioDuplicadoException extends DomainException {

    public AsesorFichaUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarAsesorFichaKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.AsesorFicha.USUARIO_DUPLICADO
        );
    }
}
