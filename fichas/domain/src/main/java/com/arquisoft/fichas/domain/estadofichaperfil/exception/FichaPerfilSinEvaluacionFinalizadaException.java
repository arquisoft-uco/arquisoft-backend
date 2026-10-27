package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class FichaPerfilSinEvaluacionFinalizadaException extends DomainException {

    public FichaPerfilSinEvaluacionFinalizadaException(UUID fichaPerfil) {
        super(BaseError.conDetalle(
                FichasCodes.EstadoFichaPerfil.SIN_EVALUACION_FINALIZADA,
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_SIN_EVALUACION_FINALIZADA),
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_SIN_EVALUACION_FINALIZADA_DETALLE, fichaPerfil)
        ));
    }
}
