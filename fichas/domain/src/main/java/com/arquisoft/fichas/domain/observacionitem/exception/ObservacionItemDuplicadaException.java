package com.arquisoft.fichas.domain.observacionitem.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;

import java.util.UUID;

public final class ObservacionItemDuplicadaException extends DomainException {

    public ObservacionItemDuplicadaException(UUID revisionItem, String observacion) {
        super(
                Mensajes.formatear(ObservacionItemKey.ERROR_OBSERVACION_ITEM_DUPLICADA, revisionItem, observacion),
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_DUPLICADA
        );
    }
}
