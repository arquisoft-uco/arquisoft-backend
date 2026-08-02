package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;
import java.util.UUID;

public final class EstudianteDuplicadoException extends ApplicationException {

    public EstudianteDuplicadoException(UUID estudianteId) {
        super(
            FichasMessages.EstudianteFichaPerfil.DUPLICADO_MSG.formatted(estudianteId),
            FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO
        );
    }
}
