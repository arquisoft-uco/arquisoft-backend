package com.arquisoft.fichas.application.evaluacionfichaperfil.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.FichasMessages;

import java.util.UUID;

public class RepresentanteComiteNoEncontradoException extends ApplicationException {

    public RepresentanteComiteNoEncontradoException(UUID representanteId) {
        super(
                FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO_MSG
                        .formatted(representanteId),
                FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO

        );
    }
}
