package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorCerradaException extends DomainException {

    public RevisionAsesorCerradaException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.CERRADA,
                Mensajes.obtener(RevisionAsesorKey.ERROR_CERRADA),
                Mensajes.formatear(RevisionAsesorKey.ERROR_CERRADA_DETALLE, revisionAsesor)
        ));
    }
}
