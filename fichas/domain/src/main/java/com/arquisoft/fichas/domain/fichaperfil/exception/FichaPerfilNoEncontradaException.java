package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class FichaPerfilNoEncontradaException extends ApplicationException {

    public FichaPerfilNoEncontradaException(UUID fichaPerfilId) {
        super(
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_NO_ENCONTRADA, fichaPerfilId),
                FichasCodes.FichaPerfil.FICHA_NO_ENCONTRADA
        );
    }
}
