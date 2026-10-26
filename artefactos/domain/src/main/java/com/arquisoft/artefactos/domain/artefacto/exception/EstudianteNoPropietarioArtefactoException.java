package com.arquisoft.artefactos.domain.artefacto.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;

import java.util.UUID;

public final class EstudianteNoPropietarioArtefactoException extends DomainException {

    public EstudianteNoPropietarioArtefactoException(UUID estudiante, UUID artefacto) {
        super(
                Mensajes.formatear(ArtefactoKey.ERROR_ESTUDIANTE_NO_PROPIETARIO, estudiante, artefacto),
                ArtefactosCodes.Artefacto.ESTUDIANTE_NO_PROPIETARIO
        );
    }
}
