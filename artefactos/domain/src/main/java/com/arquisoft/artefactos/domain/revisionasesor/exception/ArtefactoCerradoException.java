package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class ArtefactoCerradoException extends DomainException {

    public ArtefactoCerradoException(UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.Artefacto.CERRADO,
                Mensajes.obtener(RevisionAsesorKey.ERROR_ARTEFACTO_CERRADO),
                Mensajes.formatear(RevisionAsesorKey.ERROR_ARTEFACTO_CERRADO_DETALLE, revisionAsesor)
        ));
    }
}
