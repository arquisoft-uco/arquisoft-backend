package com.arquisoft.entregables.domain.estadoproyecto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EstadoProyectoKey;

public final class EstadoProyectoNoEncontradoException extends DomainException {

    public EstadoProyectoNoEncontradoException(String estado) {
        super(BaseError.conDetalle(
                EntregablesCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO,
                Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoProyectoKey.ERROR_NO_ENCONTRADO_DETALLE, estado)
        ));
    }
}
