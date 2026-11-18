package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemSinObservacionesException extends DomainException {

    public RevisionItemSinObservacionesException(UUID revisionItem) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.SIN_OBSERVACIONES,
                Mensajes.formatear(RevisionItemKey.ERROR_SIN_OBSERVACIONES),
                Mensajes.formatear(RevisionItemKey.ERROR_SIN_OBSERVACIONES_DETALLE, revisionItem)
        ));
    }
}
