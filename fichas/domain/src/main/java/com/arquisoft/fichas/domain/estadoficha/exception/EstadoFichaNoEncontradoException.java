package com.arquisoft.fichas.domain.estadoficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;

public final class EstadoFichaNoEncontradoException extends DomainException {

    public EstadoFichaNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoFicha.NO_ENCONTRADO,
                Mensajes.formatear(EstadoFichaKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EstadoFichaKey.ERROR_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
