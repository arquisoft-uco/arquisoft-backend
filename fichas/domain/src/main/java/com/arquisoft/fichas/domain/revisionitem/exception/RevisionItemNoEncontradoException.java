package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemNoEncontradoException extends DomainException {

    public RevisionItemNoEncontradoException(UUID revisionItem) {
        super(
                Mensajes.formatear(RevisionItemKey.ERROR_NO_ENCONTRADA, revisionItem),
                FichasCodes.RevisionItem.NO_ENCONTRADA
        );
    }
}
