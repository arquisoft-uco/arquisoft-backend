package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class RevisionAsesorCerradaException extends DomainException {

    public RevisionAsesorCerradaException() {
        super(
                Mensajes.obtener(RevisionAsesorKey.ERROR_CERRADA),
                ArtefactosCodes.RevisionAsesor.CERRADA
        );
    }
}
