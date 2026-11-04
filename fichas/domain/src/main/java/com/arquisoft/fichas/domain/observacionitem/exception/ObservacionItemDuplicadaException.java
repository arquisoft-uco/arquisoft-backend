package com.arquisoft.fichas.domain.observacionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;

import java.util.UUID;

public final class ObservacionItemDuplicadaException extends DomainException {

    public ObservacionItemDuplicadaException(UUID revisionItem, String observacion) {
        super(BaseError.conDetalle(
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_DUPLICADA,
                Mensajes.formatear(ObservacionItemKey.ERROR_OBSERVACION_ITEM_DUPLICADA, observacion),
                Mensajes.formatear(ObservacionItemKey.ERROR_OBSERVACION_ITEM_DUPLICADA_DETALLE, revisionItem, observacion)
        ));
    }
}
