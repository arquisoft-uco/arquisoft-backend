package com.arquisoft.fichas.domain.estudiante.exception;

import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;
import java.util.UUID;

public final class EstudianteNoEncontradoException extends ApplicationException {

    public EstudianteNoEncontradoException(UUID estudianteId) {
        super(
            Mensajes.formatear(EstudianteKey.ERROR_NO_ENCONTRADO, estudianteId),
            FichasCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO
        );
    }
}
