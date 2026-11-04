package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorConObservacionesAbiertasException extends DomainException {

    public RevisionAsesorConObservacionesAbiertasException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.CON_OBSERVACIONES_ABIERTAS,
                Mensajes.obtener(RevisionAsesorKey.ERROR_CON_OBSERVACIONES_ABIERTAS),
                Mensajes.formatear(RevisionAsesorKey.ERROR_CON_OBSERVACIONES_ABIERTAS_DETALLE, revisionAsesor)
        ));
    }
}
