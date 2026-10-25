package com.arquisoft.artefactos.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;

import java.util.UUID;

public final class AsesorDeRevisionNoEncontradoException extends DomainException {

    public AsesorDeRevisionNoEncontradoException(UUID revisionAsesor) {
        super(
                Mensajes.formatear(AsesorKey.ERROR_DE_REVISION_NO_ENCONTRADO, revisionAsesor),
                ArtefactosCodes.Asesor.DE_REVISION_NO_ENCONTRADO
        );
    }
}
