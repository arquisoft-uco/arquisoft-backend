package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class RevisionAsesorAbiertaExistenteException extends DomainException {

    public RevisionAsesorAbiertaExistenteException(UUID versionArtefacto, int numeroVersion) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.ABIERTA_EXISTENTE,
                Mensajes.formatear(RevisionAsesorKey.ERROR_ABIERTA_EXISTENTE, numeroVersion),
                Mensajes.formatear(RevisionAsesorKey.ERROR_ABIERTA_EXISTENTE_DETALLE, versionArtefacto)
        ));
    }
}
