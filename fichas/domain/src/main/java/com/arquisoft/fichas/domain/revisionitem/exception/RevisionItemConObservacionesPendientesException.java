package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemConObservacionesPendientesException extends DomainException {

    public RevisionItemConObservacionesPendientesException(UUID revisionItem, long pendientes) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.CON_OBSERVACIONES_PENDIENTES,
                Mensajes.formatear(RevisionItemKey.ERROR_OBSERVACIONES_PENDIENTES),
                Mensajes.formatear(RevisionItemKey.ERROR_OBSERVACIONES_PENDIENTES_DETALLE, revisionItem, pendientes)
        ));
    }
}
