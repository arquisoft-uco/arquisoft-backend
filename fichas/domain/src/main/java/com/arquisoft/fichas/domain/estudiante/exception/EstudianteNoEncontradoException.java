package com.arquisoft.fichas.domain.estudiante.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;
import java.util.UUID;

public final class EstudianteNoEncontradoException extends ApplicationException {

    public EstudianteNoEncontradoException(UUID estudianteId) {
        super(
            Messages.formatear(FichasKeys.Estudiante.ERROR_NO_ENCONTRADO, estudianteId),
            FichasCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO
        );
    }
}
