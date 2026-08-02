package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

public final class FichaTituloDuplicadoException extends ApplicationException {

    public FichaTituloDuplicadoException(String titulo) {
        super(
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_TITULO_DUPLICADO, titulo),
                FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO
        );
    }
}
