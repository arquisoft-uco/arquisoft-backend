package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class RevisionVersionAnteriorNoResueltaException extends DomainException {

    public RevisionVersionAnteriorNoResueltaException(int numeroVersion, RevisionAsesorDomain revision) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.VERSION_ANTERIOR_NO_RESUELTA,
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_ANTERIOR_NO_RESUELTA,
                        numeroVersion, revision.getEstado().getNombre()),
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_ANTERIOR_NO_RESUELTA_DETALLE,
                        revision.getId(), revision.getEstado().getId())
        ));
    }
}
