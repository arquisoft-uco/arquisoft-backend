package com.arquisoft.fichas.domain.evaluacionfichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;

import java.util.UUID;

public class RepresentanteComiteNoEncontradoException extends DomainException {

    public RepresentanteComiteNoEncontradoException(UUID representanteId) {
        super(BaseError.conDetalle(
                FichasCodes.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO,
                Mensajes.formatear(RepresentanteComiteKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(RepresentanteComiteKey.ERROR_NO_ENCONTRADO_DETALLE, representanteId)
        ));
    }
}
