package com.arquisoft.proyectos.domain.estadoentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.EstadoEntregableKey;

public final class EstadoEntregableNoEncontradoException extends DomainException {

    public EstadoEntregableNoEncontradoException(String estado) {
        super(BaseError.conDetalle(
                ProyectosCodes.EstadoEntregable.NO_ENCONTRADO,
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO_DETALLE, estado)
        ));
    }
}
