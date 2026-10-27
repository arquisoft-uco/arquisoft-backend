package com.arquisoft.artefactos.domain.revisionasesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;

public final class EstudianteNoParticipaArtefactoException extends DomainException {

    public EstudianteNoParticipaArtefactoException() {
        super(
                Mensajes.obtener(RevisionAsesorKey.ERROR_ESTUDIANTE_NO_PARTICIPA),
                ArtefactosCodes.RevisionAsesor.ESTUDIANTE_NO_PARTICIPA_ARTEFACTO
        );
    }
}
