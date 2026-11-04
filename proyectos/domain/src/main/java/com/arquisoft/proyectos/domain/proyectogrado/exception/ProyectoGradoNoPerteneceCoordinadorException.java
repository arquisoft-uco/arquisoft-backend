package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoPerteneceCoordinadorException extends DomainException {

    public ProyectoGradoNoPerteneceCoordinadorException(UUID proyectoGrado, UUID coordinador) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.NO_PERTENECE_COORDINADOR,
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_PERTENECE_COORDINADOR),
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_PERTENECE_COORDINADOR_DETALLE, coordinador, proyectoGrado)
        ));
    }
}
