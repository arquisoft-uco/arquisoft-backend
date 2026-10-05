package com.arquisoft.proyectos.domain.coordinador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class CoordinadorNoVigenteException extends DomainException {

    public CoordinadorNoVigenteException(UUID coordinador) {
        super(
                Mensajes.formatear(ProyectoGradoKey.ERROR_COORDINADOR_NO_VIGENTE, coordinador),
                ProyectosCodes.ProyectoGrado.COORDINADOR_NO_VIGENTE
        );
    }
}
