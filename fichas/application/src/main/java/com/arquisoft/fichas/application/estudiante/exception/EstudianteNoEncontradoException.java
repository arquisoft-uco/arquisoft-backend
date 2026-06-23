package com.arquisoft.fichas.application.estudiante.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;
import java.util.UUID;

public final class EstudianteNoEncontradoException extends ApplicationException {

    public EstudianteNoEncontradoException(UUID estudianteId) {
        super(
            FichasMessages.Estudiante.NO_ENCONTRADO.formatted(estudianteId),
            FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO
        );
    }
}
