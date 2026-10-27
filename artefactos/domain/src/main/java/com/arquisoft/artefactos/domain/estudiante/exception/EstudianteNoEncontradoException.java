package com.arquisoft.artefactos.domain.estudiante.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.EstudianteKey;

public final class EstudianteNoEncontradoException extends DomainException {

    public EstudianteNoEncontradoException() {
        super(
                Mensajes.obtener(EstudianteKey.ERROR_NO_ENCONTRADO),
                ArtefactosCodes.Estudiante.NO_ENCONTRADO
        );
    }
}
