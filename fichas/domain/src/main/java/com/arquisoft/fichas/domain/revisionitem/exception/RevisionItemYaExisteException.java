package com.arquisoft.fichas.domain.revisionitem.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

import java.util.UUID;

public final class RevisionItemYaExisteException extends DomainException {

    public RevisionItemYaExisteException(UUID item) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.YA_EXISTE,
                Mensajes.formatear(RevisionItemKey.ERROR_YA_EXISTE),
                Mensajes.formatear(RevisionItemKey.ERROR_YA_EXISTE_DETALLE, item)
        ));
    }
}
