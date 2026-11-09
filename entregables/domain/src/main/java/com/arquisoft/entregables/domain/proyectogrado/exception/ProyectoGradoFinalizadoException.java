package com.arquisoft.entregables.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoFinalizadoException extends DomainException {

    public ProyectoGradoFinalizadoException(UUID proyectoGrado, String nombre) {
        super(BaseError.conDetalle(
                EntregablesCodes.ProyectoGrado.FINALIZADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_FINALIZADO, nombre),
                Mensajes.formatear(ProyectoGradoKey.ERROR_FINALIZADO_DETALLE, proyectoGrado)
        ));
    }
}
