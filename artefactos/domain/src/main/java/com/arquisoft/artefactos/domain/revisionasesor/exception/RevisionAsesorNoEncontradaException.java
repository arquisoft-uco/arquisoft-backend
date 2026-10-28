package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorNoEncontradaException extends DomainException {

    public RevisionAsesorNoEncontradaException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.NO_ENCONTRADA,
                Mensajes.obtener(RevisionAsesorKey.ERROR_NO_ENCONTRADA),
                Mensajes.formatear(RevisionAsesorKey.ERROR_NO_ENCONTRADA_DETALLE, revisionAsesor)
        ));
    }
}
