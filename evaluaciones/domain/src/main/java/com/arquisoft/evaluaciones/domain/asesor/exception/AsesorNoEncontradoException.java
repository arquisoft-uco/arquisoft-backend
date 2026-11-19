package com.arquisoft.evaluaciones.domain.asesor.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.AsesorKey;

import java.util.UUID;

public final class AsesorNoEncontradoException extends DomainException {

    public AsesorNoEncontradoException(UUID asesor) {
        super(BaseError.conDetalle(
                EvaluacionesCodes.Asesor.NO_ENCONTRADO,
                Mensajes.formatear(AsesorKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(AsesorKey.ERROR_NO_ENCONTRADO_DETALLE, asesor)
        ));
    }
}
