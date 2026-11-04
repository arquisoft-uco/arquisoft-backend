package com.arquisoft.artefactos.domain.observacion.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ObservacionKey;

import java.util.UUID;

public final class ObservacionDuplicadaException extends DomainException {

    public ObservacionDuplicadaException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Observacion.DUPLICADA,
                Mensajes.obtener(ObservacionKey.ERROR_DUPLICADA),
                Mensajes.formatear(ObservacionKey.ERROR_DUPLICADA_DETALLE, revisionAsesor)
        ));
    }
}
