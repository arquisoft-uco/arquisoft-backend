package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoNoCancelableException extends DomainException {

    public ProyectoGradoNoCancelableException(UUID proyectoGrado, String tituloProyecto, EstadoProyecto estado) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.NO_CANCELABLE,
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_CANCELABLE, tituloProyecto,
                        estado.getNombre().toLowerCase()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_CANCELABLE_DETALLE, proyectoGrado, estado.getId())
        ));
    }
}
