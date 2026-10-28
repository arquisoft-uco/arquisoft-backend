package com.arquisoft.fichas.domain.estadorevision.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;

public final class EstadoRevisionNoEncontradoException extends DomainException {

    public EstadoRevisionNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                FichasCodes.RevisionItem.ESTADO_REVISION_NO_ENCONTRADO,
                Mensajes.formatear(RevisionItemKey.ERROR_ESTADO_NO_ENCONTRADO),
                Mensajes.formatear(RevisionItemKey.ERROR_ESTADO_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
