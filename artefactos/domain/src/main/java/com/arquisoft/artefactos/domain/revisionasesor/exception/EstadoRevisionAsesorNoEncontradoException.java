package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class EstadoRevisionAsesorNoEncontradoException extends DomainException {

    public EstadoRevisionAsesorNoEncontradoException(String id) {
        super(
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_INVALIDO, id),
                ArtefactosCodes.RevisionAsesor.ESTADO_INVALIDO
        );
    }
}
