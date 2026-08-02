package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.AuthorizationException;

import java.util.UUID;

public final class FichaNoPropietarioException extends AuthorizationException {

    public FichaNoPropietarioException(UUID fichaId, UUID estudianteId) {
        super(
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_NO_PROPIETARIO, estudianteId, fichaId),
                FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO
        );
    }
}
