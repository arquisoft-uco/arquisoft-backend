package com.arquisoft.biblioteca.domain.estadoentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EstadoEntregableKey;

public final class EstadoEntregableNoEncontradoException extends DomainException {

    public EstadoEntregableNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EstadoEntregable.NO_ENCONTRADO,
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoEntregableKey.ERROR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
