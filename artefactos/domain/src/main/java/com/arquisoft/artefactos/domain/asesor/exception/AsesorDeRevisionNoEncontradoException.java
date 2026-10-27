package com.arquisoft.artefactos.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;

public final class AsesorDeRevisionNoEncontradoException extends DomainException {

    public AsesorDeRevisionNoEncontradoException() {
        super(
                Mensajes.obtener(AsesorKey.ERROR_DE_REVISION_NO_ENCONTRADO),
                ArtefactosCodes.Asesor.DE_REVISION_NO_ENCONTRADO
        );
    }
}
