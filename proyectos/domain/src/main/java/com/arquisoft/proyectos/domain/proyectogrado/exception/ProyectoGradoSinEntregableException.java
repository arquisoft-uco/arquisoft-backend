package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoSinEntregableException extends DomainException {

    public ProyectoGradoSinEntregableException(UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.SIN_ENTREGABLE,
                Mensajes.formatear(ProyectoGradoKey.ERROR_SIN_ENTREGABLE, tituloProyecto),
                Mensajes.formatear(ProyectoGradoKey.ERROR_SIN_ENTREGABLE_DETALLE, proyectoGrado)
        ));
    }
}
