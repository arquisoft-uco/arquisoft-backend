package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

import java.util.UUID;

public final class EstudianteNoParticipaArtefactoException extends DomainException {

    public EstudianteNoParticipaArtefactoException(UUID estudiante, UUID revisionAsesor) {
        super(BaseError.conDetalle(
                ArtefactosCodes.RevisionAsesor.ESTUDIANTE_NO_PARTICIPA_ARTEFACTO,
                Mensajes.obtener(RevisionAsesorKey.ERROR_ESTUDIANTE_NO_PARTICIPA),
                Mensajes.formatear(RevisionAsesorKey.ERROR_ESTUDIANTE_NO_PARTICIPA_DETALLE,
                        estudiante, revisionAsesor)
        ));
    }
}
