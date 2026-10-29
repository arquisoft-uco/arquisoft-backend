package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class VersionPosteriorARevisionNoExisteException extends DomainException {

    public VersionPosteriorARevisionNoExisteException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.SIN_VERSION_POSTERIOR,
                Mensajes.obtener(RevisionAsesorKey.ERROR_SIN_VERSION_POSTERIOR),
                Mensajes.formatear(RevisionAsesorKey.ERROR_SIN_VERSION_POSTERIOR_DETALLE, revisionAsesor)
        ));
    }
}
