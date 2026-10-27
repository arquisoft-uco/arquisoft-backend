package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class EstadoDestinoRevisionAsesorNoPermitidoException extends DomainException {

    public EstadoDestinoRevisionAsesorNoPermitidoException(String estado) {
        super(
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, estado),
                ArtefactosCodes.RevisionAsesor.ESTADO_DESTINO_NO_PERMITIDO
        );
    }
}
