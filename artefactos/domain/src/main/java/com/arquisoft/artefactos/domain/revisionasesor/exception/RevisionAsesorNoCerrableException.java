package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorNoCerrableException extends DomainException {

    public RevisionAsesorNoCerrableException(UUID revisionAsesor, EstadoRevisionAsesor estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.NO_CERRABLE,
                Mensajes.obtener(RevisionAsesorKey.ERROR_NO_CERRABLE),
                Mensajes.formatear(RevisionAsesorKey.ERROR_NO_CERRABLE_DETALLE, revisionAsesor, estado.getId())
        ));
    }
}
