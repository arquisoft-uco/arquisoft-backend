package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class RepresentanteComiteNoEncontradoException extends DomainException {

    public RepresentanteComiteNoEncontradoException(UUID representanteId) {
        super(
                Mensajes.formatear(RepresentanteComiteKey.ERROR_NO_ENCONTRADO, representanteId),
                FichasCodes.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO

        );
    }
}
