package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoCanceladoException extends DomainException {

    public ProyectoGradoCanceladoException(UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.CANCELADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_CANCELADO, tituloProyecto),
                Mensajes.formatear(ProyectoGradoKey.ERROR_CANCELADO_DETALLE, proyectoGrado)
        ));
    }
}
