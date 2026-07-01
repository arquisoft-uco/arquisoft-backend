package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class FichaNoEncontradaException extends ApplicationException {

    public FichaNoEncontradaException(UUID fichaId) {
        super(
                FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG.formatted(fichaId),
                FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA
        );
    }
}
