package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorPendienteException extends DomainException {

    public RevisionAsesorPendienteException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.PENDIENTE,
                Mensajes.obtener(RevisionAsesorKey.ERROR_PENDIENTE),
                Mensajes.formatear(RevisionAsesorKey.ERROR_PENDIENTE_DETALLE, revisionAsesor)
        ));
    }
}
