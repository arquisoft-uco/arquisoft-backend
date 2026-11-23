package com.arquisoft.fichas.domain.estadoevaluacionficha.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;

import java.util.UUID;

public final class EstadoDescartadaNoAsignableException extends DomainException {

    public EstadoDescartadaNoAsignableException(UUID evaluacionFichaPerfil) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoEvaluacionFicha.DESCARTADA_NO_ASIGNABLE,
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_DESCARTADA_NO_ASIGNABLE),
                Mensajes.formatear(EstadoEvaluacionFichaKey.ERROR_DESCARTADA_NO_ASIGNABLE_DETALLE,
                        evaluacionFichaPerfil)
        ));
    }
}
