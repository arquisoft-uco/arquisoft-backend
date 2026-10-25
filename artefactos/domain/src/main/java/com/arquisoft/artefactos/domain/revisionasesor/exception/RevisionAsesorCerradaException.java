package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorCerradaException extends DomainException {

    public RevisionAsesorCerradaException(UUID revisionAsesor) {
        super(
                Mensajes.formatear(RevisionAsesorKey.ERROR_CERRADA, revisionAsesor),
                ArtefactosCodes.RevisionAsesor.CERRADA
        );
    }
}
