package com.arquisoft.usuarios.domain.asesorficha.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorFichaKey;

import java.util.UUID;

public final class AsesorFichaNoEncontradoException extends DomainException {

    public AsesorFichaNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverAsesorFichaKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.AsesorFicha.ASESOR_FICHA_NO_ENCONTRADO
        );
    }
}
