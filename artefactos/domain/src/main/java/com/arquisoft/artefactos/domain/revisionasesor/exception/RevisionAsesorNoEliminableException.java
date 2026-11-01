package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorNoEliminableException extends DomainException {

    public RevisionAsesorNoEliminableException(UUID revisionAsesor, EstadoRevisionAsesor estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.NO_ELIMINABLE,
                Mensajes.obtener(RevisionAsesorKey.ERROR_NO_ELIMINABLE),
                Mensajes.formatear(RevisionAsesorKey.ERROR_NO_ELIMINABLE_DETALLE,
                        revisionAsesor, estado.getId())
        ));
    }
}
