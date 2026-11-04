package com.arquisoft.proyectos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoFinalizadoException extends DomainException {

    public ProyectoGradoFinalizadoException(UUID proyectoGrado, String tituloProyecto) {
        super(BaseError.conDetalle(
                ProyectosCodes.ProyectoGrado.FINALIZADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_FINALIZADO, tituloProyecto),
                Mensajes.formatear(ProyectoGradoKey.ERROR_FINALIZADO_DETALLE, proyectoGrado)
        ));
    }
}
