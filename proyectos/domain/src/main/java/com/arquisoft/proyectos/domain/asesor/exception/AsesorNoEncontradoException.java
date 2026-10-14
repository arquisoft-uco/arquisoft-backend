package com.arquisoft.proyectos.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;

import java.util.UUID;

public final class AsesorNoEncontradoException extends DomainException {

    public AsesorNoEncontradoException(UUID asesor) {
        super(
                Mensajes.formatear(AsesorKey.ERROR_NO_ENCONTRADO, asesor),
                ProyectosCodes.Asesor.NO_ENCONTRADO
        );
    }
}
