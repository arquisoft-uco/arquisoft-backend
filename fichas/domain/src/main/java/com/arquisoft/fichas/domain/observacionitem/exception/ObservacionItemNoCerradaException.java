package com.arquisoft.fichas.domain.observacionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;

import java.util.UUID;

public final class ObservacionItemNoCerradaException extends DomainException {

    public ObservacionItemNoCerradaException(UUID observacionItem, String estado) {
        super(BaseError.conDetalle(
                FichasCodes.ObservacionItem.NO_CERRADA,
                Mensajes.formatear(ObservacionItemKey.ERROR_NO_CERRADA),
                Mensajes.formatear(ObservacionItemKey.ERROR_NO_CERRADA_DETALLE, observacionItem, estado)
        ));
    }
}
