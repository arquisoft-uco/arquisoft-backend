package com.arquisoft.fichas.domain.estudiantefichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;
import java.util.UUID;

public final class EstudianteDuplicadoException extends ApplicationException {

    public EstudianteDuplicadoException(UUID estudianteId) {
        super(
            Messages.formatear(FichasKeys.EstudianteFichaPerfil.ERROR_DUPLICADO, estudianteId),
            FichasCodes.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO
        );
    }
}
