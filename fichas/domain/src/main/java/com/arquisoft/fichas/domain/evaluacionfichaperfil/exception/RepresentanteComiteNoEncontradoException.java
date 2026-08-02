package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public class RepresentanteComiteNoEncontradoException extends ApplicationException {

    public RepresentanteComiteNoEncontradoException(UUID representanteId) {
        super(
                Messages.obtener(FichasKeys.RepresentanteComite.ERROR_NO_ENCONTRADO)
                        .formatted(representanteId),
                FichasCodes.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO

        );
    }
}
