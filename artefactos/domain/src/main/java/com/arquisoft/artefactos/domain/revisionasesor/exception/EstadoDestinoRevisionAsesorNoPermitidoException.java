package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class EstadoDestinoRevisionAsesorNoPermitidoException extends DomainException {

    public EstadoDestinoRevisionAsesorNoPermitidoException(UUID revisionAsesor, String estado) {
        super(
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTADO_DESTINO_NO_PERMITIDO, revisionAsesor, estado),
                ArtefactosCodes.RevisionAsesor.ESTADO_DESTINO_NO_PERMITIDO
        );
    }
}
