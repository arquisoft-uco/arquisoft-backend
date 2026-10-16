package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class FichaPerfilConEvaluacionEnCursoException extends DomainException {

    public FichaPerfilConEvaluacionEnCursoException(UUID fichaPerfil, long enEvaluacion) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_EVALUACION_EN_CURSO, fichaPerfil, enEvaluacion),
                FichasCodes.EstadoFichaPerfil.EVALUACION_EN_CURSO
        );
    }
}
