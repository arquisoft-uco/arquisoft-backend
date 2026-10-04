package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class AprobacionSinEvaluacionAprobatoriaException extends DomainException {

    public AprobacionSinEvaluacionAprobatoriaException(UUID fichaPerfil) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_APROBACION_SIN_EVALUACION_APROBATORIA, fichaPerfil),
                FichasCodes.EstadoFichaPerfil.APROBACION_SIN_EVALUACION_APROBATORIA
        );
    }
}
