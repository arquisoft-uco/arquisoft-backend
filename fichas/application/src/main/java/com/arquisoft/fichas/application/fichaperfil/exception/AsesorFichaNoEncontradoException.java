package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public class AsesorFichaNoEncontradoException extends ApplicationException {

    public AsesorFichaNoEncontradoException(UUID id) {
        super(
                FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG.formatted(id),
                FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO
        );
    }
}
