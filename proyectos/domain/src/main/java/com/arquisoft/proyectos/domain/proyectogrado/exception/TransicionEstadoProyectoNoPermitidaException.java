package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class TransicionEstadoProyectoNoPermitidaException extends DomainException {

    public TransicionEstadoProyectoNoPermitidaException(UUID proyectoGrado, String tituloProyecto,
                                                        EstadoProyecto actual, EstadoProyecto nuevo) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.TRANSICION_ESTADO_NO_PERMITIDA,
                Mensajes.formatear(ProyectoGradoKey.ERROR_TRANSICION_ESTADO_NO_PERMITIDA,
                        tituloProyecto, actual.getNombre(), nuevo.getNombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_TRANSICION_ESTADO_NO_PERMITIDA_DETALLE,
                        proyectoGrado, actual.getId(), nuevo.getId())
        ));
    }
}
