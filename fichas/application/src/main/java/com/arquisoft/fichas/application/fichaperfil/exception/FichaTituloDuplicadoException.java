package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

public final class FichaTituloDuplicadoException extends ApplicationException {

    public FichaTituloDuplicadoException(String titulo) {
        super(
                FichasMessages.FichaPerfil.TITULO_DUPLICADO.formatted(titulo),
                FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO
        );
    }
}
