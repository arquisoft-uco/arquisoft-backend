package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class RevisionAsesorNoEncontradaException extends DomainException {

    public RevisionAsesorNoEncontradaException() {
        super(
                Mensajes.obtener(RevisionAsesorKey.ERROR_NO_ENCONTRADA),
                ArtefactosCodes.RevisionAsesor.NO_ENCONTRADA
        );
    }
}
