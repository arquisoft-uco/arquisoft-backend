package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class VersionAnteriorSinRevisionException extends DomainException {

    public VersionAnteriorSinRevisionException(int numeroVersion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.VERSION_ANTERIOR_SIN_REVISION,
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_ANTERIOR_SIN_REVISION, numeroVersion),
                Mensajes.formatear(RevisionAsesorKey.ERROR_VERSION_ANTERIOR_SIN_REVISION_DETALLE, numeroVersion)
        ));
    }
}
