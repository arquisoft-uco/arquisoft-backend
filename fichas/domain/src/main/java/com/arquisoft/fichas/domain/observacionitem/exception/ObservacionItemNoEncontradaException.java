package com.arquisoft.fichas.domain.observacionitem.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;

import java.util.UUID;

public final class ObservacionItemNoEncontradaException extends DomainException {

    public ObservacionItemNoEncontradaException(UUID observacionItem) {
        super(
                Mensajes.formatear(ObservacionItemKey.ERROR_NO_ENCONTRADA, observacionItem),
                FichasCodes.ObservacionItem.NO_ENCONTRADA
        );
    }
}
