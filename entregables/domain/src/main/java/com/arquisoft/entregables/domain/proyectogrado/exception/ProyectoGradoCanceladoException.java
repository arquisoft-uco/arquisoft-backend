package com.arquisoft.entregables.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;

import java.util.UUID;

public final class ProyectoGradoCanceladoException extends DomainException {

    public ProyectoGradoCanceladoException(UUID proyectoGrado, String nombre) {
        super(BaseError.conDetalle(
                EntregablesCodes.ProyectoGrado.CANCELADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_CANCELADO, nombre),
                Mensajes.formatear(ProyectoGradoKey.ERROR_CANCELADO_DETALLE, proyectoGrado)
        ));
    }
}
