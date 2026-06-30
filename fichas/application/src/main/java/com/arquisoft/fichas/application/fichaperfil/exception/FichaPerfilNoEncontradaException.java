package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class FichaPerfilNoEncontradaException extends ApplicationException {

    public FichaPerfilNoEncontradaException(UUID fichaPerfilId) {
        super(
                FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG.formatted(fichaPerfilId),
                FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA
        );
    }
}
