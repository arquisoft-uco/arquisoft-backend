package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemNoEnProgresoException extends DomainException {

    public RevisionItemNoEnProgresoException(UUID revisionItem) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.NO_EN_PROGRESO,
                Mensajes.formatear(RevisionItemKey.ERROR_NO_EN_PROGRESO),
                Mensajes.formatear(RevisionItemKey.ERROR_NO_EN_PROGRESO_DETALLE, revisionItem)
        ));
    }
}
