package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemNoEncontradoException extends DomainException {

    public RevisionItemNoEncontradoException(UUID revisionItem) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.NO_ENCONTRADA,
                Mensajes.formatear(RevisionItemKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(RevisionItemKey.ERROR_NO_ENCONTRADA_DETALLE, revisionItem)
        ));
    }
}
