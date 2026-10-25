package com.arquisoft.artefactos.domain.estudiante.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;

import java.util.UUID;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException(UUID estudiante) {
        super(
                Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADO, estudiante),
                ArtefactosCodes.Estudiante.NO_ENCONTRADO
        );
    }
}
