package com.arquisoft.fichas.application.fichaperfil.exception;

import com.arquisoft.shared.exception.AuthorizationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public final class FichaNoPropietarioException extends AuthorizationException {

    public FichaNoPropietarioException(UUID fichaId, UUID estudianteId) {
        super(
                FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO_MSG.formatted(estudianteId, fichaId),
                FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO
        );
    }
}
