package com.arquisoft.fichas.domain.estadoobservacionrevision.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;

public final class EstadoObservacionRevisionNoEncontradoException extends DomainException {

    public EstadoObservacionRevisionNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                FichasCodes.ObservacionItem.ESTADO_OBSERVACION_REVISION_NO_ENCONTRADO,
                Mensajes.formatear(ObservacionItemKey.ERROR_ESTADO_NO_ENCONTRADO),
                Mensajes.formatear(ObservacionItemKey.ERROR_ESTADO_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
