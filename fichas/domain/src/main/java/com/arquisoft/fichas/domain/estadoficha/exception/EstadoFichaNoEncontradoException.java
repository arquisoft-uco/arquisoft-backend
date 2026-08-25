package com.arquisoft.fichas.domain.estadoficha.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;

public final class EstadoFichaNoEncontradoException extends DomainException {

    public EstadoFichaNoEncontradoException(String id) {
        super(
                Mensajes.formatear(EstadoFichaKey.ERROR_NO_ENCONTRADO, id),
                FichasCodes.EstadoFicha.NO_ENCONTRADO
        );
    }
}
