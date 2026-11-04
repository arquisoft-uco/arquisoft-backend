package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorSinObservacionesException extends DomainException {

    public RevisionAsesorSinObservacionesException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.SIN_OBSERVACIONES,
                Mensajes.obtener(RevisionAsesorKey.ERROR_SIN_OBSERVACIONES),
                Mensajes.formatear(RevisionAsesorKey.ERROR_SIN_OBSERVACIONES_DETALLE, revisionAsesor)
        ));
    }
}
