package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemNoCorreccionDisponibleException extends DomainException {

    public RevisionItemNoCorreccionDisponibleException(UUID revisionItem) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.NO_CORRECCION_DISPONIBLE,
                Mensajes.formatear(RevisionItemKey.ERROR_NO_CORRECCION_DISPONIBLE),
                Mensajes.formatear(RevisionItemKey.ERROR_NO_CORRECCION_DISPONIBLE_DETALLE, revisionItem)
        ));
    }
}
