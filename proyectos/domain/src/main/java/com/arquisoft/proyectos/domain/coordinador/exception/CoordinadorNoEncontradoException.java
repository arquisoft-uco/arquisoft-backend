package com.arquisoft.proyectos.domain.coordinador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;

import java.util.UUID;

public final class CoordinadorNoEncontradoException extends DomainException {

    public CoordinadorNoEncontradoException(UUID coordinador) {
        super(BaseError.conDetalle(
                ProyectosCodes.Coordinador.NO_ENCONTRADO,
                Mensajes.formatear(CoordinadorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(CoordinadorKey.ERROR_NO_ENCONTRADO_DETALLE, coordinador)
        ));
    }
}
