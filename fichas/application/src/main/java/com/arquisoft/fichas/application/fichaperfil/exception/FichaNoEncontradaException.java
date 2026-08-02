package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class FichaNoEncontradaException extends ApplicationException {

    public FichaNoEncontradaException(UUID fichaId) {
        super(
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_NO_ENCONTRADA, fichaId),
                FichasCodes.FichaPerfil.FICHA_NO_ENCONTRADA
        );
    }
}
