package com.arquisoft.entregables.domain.estadoentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableKey;

public final class EstadoEntregableNoEncontradoException extends DomainException {

    public EstadoEntregableNoEncontradoException(String estado) {
        super(BaseError.conDetalle(
                EntregablesCodes.EstadoEntregable.NO_ENCONTRADO,
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO_DETALLE, estado)
        ));
    }
}
