package com.arquisoft.artefactos.domain.asesor.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.AsesorKey;

import java.util.UUID;

public final class AsesorNoVigenteException extends DomainException {

    public AsesorNoVigenteException(UUID asesor) {
        super(
                Mensajes.formatear(AsesorKey.ERROR_NO_VIGENTE, asesor),
                ArtefactosCodes.Asesor.NO_VIGENTE
        );
    }
}
